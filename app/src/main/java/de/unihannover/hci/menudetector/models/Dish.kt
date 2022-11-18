package de.unihannover.hci.menudetector.models


data class Dish(
    val name: String,
    val price: Double,
    val detail: DishDetail?,
)