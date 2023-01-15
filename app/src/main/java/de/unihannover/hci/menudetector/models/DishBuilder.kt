package de.unihannover.hci.menudetector.models

// Java
import java.util.UUID


class DishBuilder(
    private val name: String,
    private val originalName: String,
    private val price: Double,
) {

    /* ATTRIBUTES */

    private var id: String = UUID.randomUUID().toString()
    private var language: String? = null
    private var quantity: Int = 0
    private var details: DishDetails? = null


    /* METHODS */

    fun id(id: String) = apply { this.id = id }
    fun language(language: String?) = apply { this.language = language }
    fun quantity(quantity: Int) = apply { this.quantity = quantity }
    fun details(details: DishDetails?) = apply { this.details = details }

    fun build() = Dish(id, name, originalName, language, price, quantity, details)

}