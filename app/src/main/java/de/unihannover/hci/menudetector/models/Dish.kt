package de.unihannover.hci.menudetector.models

// Android
import androidx.annotation.IntRange


data class Dish(
    val id: String,
    val name: String,
    val price: Double,

    @IntRange(from = 0)
    val quantity: Int,

    val details: DishDetails? = null,
)