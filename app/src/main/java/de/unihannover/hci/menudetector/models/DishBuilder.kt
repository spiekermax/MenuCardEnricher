package de.unihannover.hci.menudetector.models

// Java
import java.util.UUID


class DishBuilder(
    private val name: String,
    private val price: Double,
) {

    /* ATTRIBUTES */

    private var id: String = UUID.randomUUID().toString()
    private var quantity: Int = 0
    private var details: DishDetails? = null


    /* METHODS */

    fun id(id: String) = apply { this.id = id }
    fun quantity(quantity: Int) = apply { this.quantity = quantity }
    fun details(details: DishDetails) = apply { this.details = details }

    fun build() = Dish(id, name, price, quantity, details)

}