package de.unihannover.hci.menudetector.models.recognition

// Internal dependencies
import de.unihannover.hci.menudetector.models.Dish


data class MenuRecognitionResult(
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