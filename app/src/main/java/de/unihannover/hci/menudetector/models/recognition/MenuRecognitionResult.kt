package de.unihannover.hci.menudetector.models.recognition

// Google
import com.google.mlkit.vision.text.Text

// Internal dependencies
import de.unihannover.hci.menudetector.models.Dish


data class MenuRecognitionResult(
    val text: Text,
    val dishes: List<DishRecognitionResult>,
) {

    /* UTILITY */

    fun isEmpty(): Boolean = dishes.isEmpty()

    fun toDishes(): List<Dish> {
        return dishes.map {
            it.toDish()
        }
    }

}