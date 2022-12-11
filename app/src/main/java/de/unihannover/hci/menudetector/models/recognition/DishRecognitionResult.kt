package de.unihannover.hci.menudetector.models.recognition

// Internal dependencies
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishBuilder


data class DishRecognitionResult(
    val name: String,
    val price: Double,
) {

    /* UTILITY */

    fun toDish(): Dish {
        return DishBuilder(name, price).build()
    }

}