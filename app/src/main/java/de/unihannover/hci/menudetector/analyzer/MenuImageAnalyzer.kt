package de.unihannover.hci.menudetector.analyzer

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
import com.google.mlkit.vision.text.Text.TextBlock
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

// Internal dependencies
import de.unihannover.hci.menudetector.models.image.ImageProperties
import de.unihannover.hci.menudetector.models.recognition.DishRecognitionResult
import de.unihannover.hci.menudetector.models.recognition.MenuRecognitionResult
import de.unihannover.hci.menudetector.util.zip


private const val UNIDENTIFIED_LANGUAGE: String = "und"
private const val LINE_MATCHING_TOLERANCE: Float = 0.5f

private val PRICE_REGEX: Regex = Regex("(USD|EUR|€|\\\$)\\s?(\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})|(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)\\s?(USD|EUR|€|\\\$)")
private val CURRENCY_REGEX: Regex = Regex("(USD|EUR|€|\\\$)")

class MenuImageAnalyzer : ImageAnalysis.Analyzer {

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
    var text: Text?
        get() = _text.value
        private set(value) {
            _text.postValue(value)
        }
    val textChanges: LiveData<Text?> = _text

    val language: String?
        get() = languageChanges.value
    val languageChanges: LiveData<String?> = Transformations.map(textChanges) {
        if (it == null) {
            null
        } else {
            identifyLanguage(it)
        }
    }

    private val prices: List<TextBlock>
        get() = pricesChanges.value!!
    private val pricesChanges: LiveData<List<TextBlock>> = Transformations.map(textChanges) {
        if (it == null) {
            listOf()
        } else {
            identifyPrices(it)
        }
    }

    private val dishes: List<DishRecognitionResult>
        get() = dishesChanges.value!!
    private val dishesChanges: LiveData<List<DishRecognitionResult>> = Transformations.map(
        zip(textChanges, pricesChanges),
    ) {
        val text: Text? = it.first
        val prices: List<TextBlock> = it.second

        if (text == null) {
            listOf()
        } else {
            identifyDishes(text, prices)
        }
    }

    val menu: MenuRecognitionResult
        get() = menuChanges.value!!
    val menuChanges: LiveData<MenuRecognitionResult> = Transformations.map(dishesChanges) {
        identifyMenu(it)
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
        val textBlocksWithIdentifiedLanguage: List<TextBlock> = textBlocks.filter {
            it.recognizedLanguage != UNIDENTIFIED_LANGUAGE
        }

        if (textBlocksWithIdentifiedLanguage.isEmpty()) return null

        val languageFrequencies: Map<String, Int> = textBlocksWithIdentifiedLanguage.groupingBy {
            it.recognizedLanguage
        }.eachCount()

        return languageFrequencies.maxBy { it.value }.key
    }

    private fun identifyPrices(text: Text): List<TextBlock> {
        val textBlocks: List<TextBlock> = text.textBlocks
        return textBlocks.filter {
            val content: String = it.text
            PRICE_REGEX.containsMatchIn(content)
        }
    }

    private fun identifyDishes(text: Text, prices: List<TextBlock>): List<DishRecognitionResult> {
        val textBlocks: List<TextBlock> = text.textBlocks

        val dishes: MutableList<DishRecognitionResult> = mutableListOf()
        for (price in prices) {
            val priceValue: Double = PRICE_REGEX.find(price.text)?.groupValues?.find {
                it.trim().isNotEmpty() && !it.contains(CURRENCY_REGEX)
            }?.toDoubleOrNull() ?: continue

            val boundingBox: Rect = price.boundingBox ?: continue
            val upperBoundary: Int = boundingBox.top
            val lowerBoundary: Int = boundingBox.bottom

            var match: TextBlock? = textBlocks.filter {
                if (it === price) return@filter false

                val textBlockBoundingBox: Rect = it.boundingBox ?: return@filter false
                val textBlockUpper: Int = textBlockBoundingBox.top
                val textBlockLower: Int = textBlockBoundingBox.bottom

                return@filter textBlockUpper <= upperBoundary && textBlockLower >= lowerBoundary
            }.getOrNull(0)

            if (match != null) {
                val name: String = match.text
                dishes.add(DishRecognitionResult(name, priceValue))

                continue
            }

            val height: Int = abs(upperBoundary - lowerBoundary)
            val scaledUpperBoundary = upperBoundary + height * LINE_MATCHING_TOLERANCE
            val scaledLowerBoundary = lowerBoundary - height * LINE_MATCHING_TOLERANCE

            match = textBlocks.filter {
                if (it === price) return@filter false

                val textBlockBoundingBox: Rect = it.boundingBox ?: return@filter false
                val textBlockUpper: Int = textBlockBoundingBox.top
                val textBlockLower: Int = textBlockBoundingBox.bottom

                return@filter textBlockUpper <= scaledUpperBoundary && textBlockLower >= scaledLowerBoundary
            }.getOrNull(0)

            if (match == null) continue

            val name: String = match.text.lines()[0]
            dishes.add(DishRecognitionResult(name, priceValue))
        }

        return dishes
    }

    private fun identifyMenu(dishes: List<DishRecognitionResult>): MenuRecognitionResult {
        return MenuRecognitionResult(dishes)
    }

}