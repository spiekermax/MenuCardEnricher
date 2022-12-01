package de.unihannover.hci.menudetector.analyzer

// Android
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

// Google
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

// Internal dependencies
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishBuilder


class MenuImageAnalyzer(
    private val menuRecognizedListener: (List<Dish>, Float?) -> Unit,
) : ImageAnalysis.Analyzer {

    /* ATTRIBUTES */

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


    /* METHODS */

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let {
            val inputImage = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
            process(inputImage, imageProxy)
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
        val dishes: MutableList<Dish> = mutableListOf()
        val confidences: MutableList<Float> = mutableListOf()

        var price: Double? = null
        for (textBlock in text.textBlocks) {
            // Calculate confidence
            val confidence: Float = textBlock.lines.fold(0f) { confidence, line ->
                confidence + line.confidence
            } / textBlock.lines.size
            confidences.add(confidence)

            // Assemble dish information
            if (price == null) {
                price = textBlock.text.toDoubleOrNull() ?: continue
            } else {
                val name: String = textBlock.text
                if (name.toDoubleOrNull() == null) {
                    val dish: Dish = DishBuilder(name, price).build()
                    dishes.add(dish)
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

        menuRecognizedListener(dishes, confidence)
    }

}