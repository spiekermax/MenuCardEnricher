package de.unihannover.hci.menudetector.analyzer

// Android
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

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


class MenuImageAnalyzer(
    private val imagePropertiesListener: ((ImageProperties) -> Unit)? = null,
    private val menuRecognizedListener: ((MenuRecognitionResult, Float?) -> Unit)? = null,
) : ImageAnalysis.Analyzer {

    /* ATTRIBUTES */

    private var hasPropagatedImageProperties = false

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


    /* METHODS */

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let {
            val inputImage = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
            process(inputImage, imageProxy)
        }

        if (!hasPropagatedImageProperties) {
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

            imagePropertiesListener?.invoke(ImageProperties(
                width,
                height,
                rotation,
                isMirrored = false,
            ))

            hasPropagatedImageProperties = true
        }
    }

    private fun process(image: InputImage, imageProxy: ImageProxy) {
        textRecognizer.process(image)
            .addOnSuccessListener {
                imageProxy.close()
                parse(it)
            }
            .addOnFailureListener {
                imageProxy.close()
                throw RuntimeException("Failed to process image.")
            }
    }

    private fun parse(text: Text) {
        val dishRecognitionResults: MutableList<DishRecognitionResult> = mutableListOf()
        val confidences: MutableList<Float> = mutableListOf()

        var price: Double? = null
        var priceTextBlock: TextBlock? = null
        val usedTextBlocks: MutableList<TextBlock> = mutableListOf()
        for (textBlock in text.textBlocks) {
            // Calculate confidence
            val confidence: Float = textBlock.lines.fold(0f) { confidence, line ->
                confidence + line.confidence
            } / textBlock.lines.size
            confidences.add(confidence)

            // Assemble dish information
            if (price == null) {
                price = textBlock.text.toDoubleOrNull() ?: continue
                priceTextBlock = textBlock
            } else {
                val name: String = textBlock.text
                if (name.toDoubleOrNull() == null) {
                    usedTextBlocks.add(textBlock)
                    usedTextBlocks.add(priceTextBlock!!)

                    val dishRecognitionResult = DishRecognitionResult(name, price)
                    dishRecognitionResults.add(dishRecognitionResult)
                }

                price = null
            }
        }

        var confidence: Float? = null
        if (confidences.size > 0) {
            confidence = confidences.reduce { total, current ->
                total + current
            } / confidences.size
        }

        val usedText = Text(text.text, usedTextBlocks)

        val result = MenuRecognitionResult(usedText, dishRecognitionResults)
        menuRecognizedListener?.invoke(result, confidence)
    }

}