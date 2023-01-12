package de.unihannover.hci.menudetector.analyzers

// Kotlin
import kotlin.math.abs

// Android
import android.graphics.Rect
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

// Google
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.Line
import com.google.mlkit.vision.text.Text.TextBlock
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

// Internal dependencies
import de.unihannover.hci.menudetector.models.image.ImageProperties
import de.unihannover.hci.menudetector.models.recognition.DishRecognitionResult
import de.unihannover.hci.menudetector.models.recognition.MenuRecognitionResult
import de.unihannover.hci.menudetector.util.Constants


private const val LINE_MATCHING_TOLERANCE: Float = 0.75f

private val PRICE_REGEX: Regex = Regex("(USD|EUR|€|\\\$)\\s?(\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})|(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)\\s?(USD|EUR|€|\\\$)")
private val CURRENCY_REGEX: Regex = Regex("(USD|EUR|€|\\\$)")

class MenuImageAnalyzer : ImageAnalysis.Analyzer {

    /* COMPANION */

    private companion object {
        fun isLanguageSupported(language: String): Boolean {
            return Constants.SUPPORTED_LANGUAGES.contains(language)
        }
    }


    /* ATTRIBUTES */

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val _imageProperties: MutableLiveData<ImageProperties?> = MutableLiveData(null)
    var imageProperties: ImageProperties?
        get() = _imageProperties.value
        private set(value) {
            _imageProperties.postValue(value)
        }
    val imagePropertiesChanges: LiveData<ImageProperties?> = _imageProperties

    private val _text: MutableLiveData<Text?> = MutableLiveData(null)
    private var text: Text?
        get() = _text.value
        private set(value) {
            _text.postValue(value)
        }
    private val textChanges: LiveData<Text?> = _text

    val menu: MenuRecognitionResult
        get() = menuChanges.value!!
    val menuChanges: LiveData<MenuRecognitionResult> = Transformations.map(textChanges) {
        if (it == null) {
            MenuRecognitionResult()
        } else {
            identifyMenu(it)
        }
    }


    /* METHODS */

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let {
            val inputImage = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
            processImage(inputImage, imageProxy)
        }

        val newImageProperties: ImageProperties = analyzeImageProperties(imageProxy)
        if (newImageProperties != imageProperties) {
            imageProperties = newImageProperties
        }
    }

    private fun analyzeImageProperties(imageProxy: ImageProxy): ImageProperties {
        val width: Int
        val height: Int
        val rotation: Int = imageProxy.imageInfo.rotationDegrees

        if (rotation == 0 || rotation == 180) {
            width = imageProxy.width
            height = imageProxy.height
        } else {
            width = imageProxy.height
            height = imageProxy.width
        }

        return ImageProperties(width, height, rotation, isMirrored = false)
    }

    private fun processImage(image: InputImage, imageProxy: ImageProxy) {
        textRecognizer.process(image)
            .addOnSuccessListener {
                imageProxy.close()
                text = it
            }
            .addOnFailureListener {
                imageProxy.close()
                throw RuntimeException("Failed to process image.")
            }
    }

    private fun identifyLanguage(text: Text): String? {
        val textBlocks: List<TextBlock> = text.textBlocks
        val lines: List<Line> = textBlocks.flatMap { it.lines }

        val linesWithSupportedLanguage: List<Line> = lines.filter {
            isLanguageSupported(it.recognizedLanguage)
        }

        if (linesWithSupportedLanguage.isEmpty()) return null

        val languageFrequencies: Map<String, Int> = linesWithSupportedLanguage.groupingBy {
            it.recognizedLanguage
        }.eachCount()

        val identifiedLanguage: String = languageFrequencies.maxBy { it.value }.key

        // Temporary workaround: Prevent false detection of dutch language
        if (identifiedLanguage == "nl") {
            return "de"
        } else {
            return identifiedLanguage
        }
    }

    private fun identifyDishes(text: Text): List<DishRecognitionResult> {
        val textBlocks: List<TextBlock> = text.textBlocks
        val lines: List<Line> = textBlocks.flatMap { it.lines }

        val prices: List<Line> = lines.filter {
            val content: String = it.text
            PRICE_REGEX.containsMatchIn(content)
        }
        val nonPrices: List<Line> = lines.filter { !prices.contains(it) }

        val dishes: MutableList<DishRecognitionResult> = mutableListOf()
        for (price in prices) {
            val priceValue: Double = PRICE_REGEX.find(price.text)?.groupValues?.find {
                it.trim().isNotEmpty() && !it.contains(CURRENCY_REGEX)
            }?.toDoubleOrNull() ?: continue

            val boundingBox: Rect = price.boundingBox ?: continue
            val upperBoundary: Int = boundingBox.top
            val lowerBoundary: Int = boundingBox.bottom

            var match: Line? = nonPrices.filter {
                val textBlockBoundingBox: Rect = it.boundingBox ?: return@filter false
                val textBlockUpper: Int = textBlockBoundingBox.top
                val textBlockLower: Int = textBlockBoundingBox.bottom

                return@filter textBlockUpper >= upperBoundary && textBlockLower <= lowerBoundary
            }.minByOrNull { it.boundingBox!!.top }

            if (match != null) {
                val name: String = match.text

                val combinedBoundingBox = Rect(match.boundingBox)
                combinedBoundingBox.union(boundingBox)
                val combinedConfidence = match.confidence * price.confidence

                dishes.add(DishRecognitionResult(
                    name = name,
                    price = priceValue,
                    boundingBox = combinedBoundingBox,
                    confidence = combinedConfidence,
                ))

                continue
            }

            val height: Int = abs(upperBoundary - lowerBoundary)
            val scaledUpperBoundary = upperBoundary - (1.0 * height * LINE_MATCHING_TOLERANCE)
            val scaledLowerBoundary = lowerBoundary + (1.5 * height * LINE_MATCHING_TOLERANCE)

            match = nonPrices.filter {
                val textBlockBoundingBox: Rect = it.boundingBox ?: return@filter false
                val textBlockUpper: Int = textBlockBoundingBox.top
                val textBlockLower: Int = textBlockBoundingBox.bottom

                return@filter textBlockUpper >= scaledUpperBoundary && textBlockLower <= scaledLowerBoundary
            }.minByOrNull { it.boundingBox!!.top }

            if (match == null) continue

            val name: String = match.text

            val combinedBoundingBox = Rect(match.boundingBox)
            combinedBoundingBox.union(boundingBox)
            val combinedConfidence = match.confidence * price.confidence

            dishes.add(DishRecognitionResult(
                name = name,
                price = priceValue,
                boundingBox = combinedBoundingBox,
                confidence = combinedConfidence,
            ))
        }

        return dishes
    }

    private fun identifyMenu(text: Text): MenuRecognitionResult {
        val language: String? = identifyLanguage(text)
        val dishes: List<DishRecognitionResult> = identifyDishes(text)

        return MenuRecognitionResult(
            language = language,
            dishes = dishes,
        )
    }

}