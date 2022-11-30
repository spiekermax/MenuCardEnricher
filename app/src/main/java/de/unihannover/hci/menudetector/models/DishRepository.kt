package de.unihannover.hci.menudetector.models

// Android
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations


class DishRepository(dishes: List<Dish> = listOf()) {

    /* ATTRIBUTES */

    private val _dishes: MutableLiveData<Map<String, Dish>> = MutableLiveData(
        dishes.associateBy { it.id }
    )

    var dishes: List<Dish>
        get() = _dishes.value!!.map {
            it.value
        }
        set(value) {
            _dishes.value = value.associateBy { it.id }
        }

    val dishChanges: LiveData<List<Dish>> = Transformations.map(_dishes) { dishes ->
        dishes.map { it.value }
    }


    /* METHODS */

    /**
     * Adds a dish to the repository.
     * Throws an exception if the dish already exists.
     */
    fun addDish(dish: Dish) {
        val newDishes: MutableMap<String, Dish> = _dishes.value!!.toMutableMap()
        newDishes.compute(dish.id) { _, value ->
            if (value == null) dish
            else throw IllegalArgumentException("Dish already exists in menu.")
        }

        _dishes.value = newDishes.toMap()
    }

    /**
     * Adds a dish to the repository if it didn't exist before.
     * Updates the dish if it existed in the repository before.
     */
    fun putDish(dish: Dish) {
        val newDishes: MutableMap<String, Dish> = _dishes.value!!.toMutableMap()
        newDishes.compute(dish.id) { _, _ -> dish }

        _dishes.value = newDishes.toMap()
    }

    /**
     * Updates a dish in the repository.
     * Throws an exception if the dish doesn't exist.
     */
    fun updateDish(dish: Dish) {
        val currentDishes: Map<String, Dish> = _dishes.value!!
        if (!currentDishes.containsKey(dish.id))
            throw IllegalArgumentException("Dish doesn't exist in menu.")

        val newDishes: MutableMap<String, Dish> = currentDishes.toMutableMap()
        newDishes.compute(dish.id) { _, _ -> dish }

        _dishes.value = newDishes.toMap()
    }

    /**
     * Removes a dish from the repository.
     */
    fun removeDish(dish: Dish) {
        val newDishes: MutableMap<String, Dish> = _dishes.value!!.toMutableMap()
        newDishes.compute(dish.id) { _, _ -> null }

        _dishes.value = newDishes.toMap()
    }


    /* UTILITY */

    fun copy(dishes: List<Dish> = this.dishes): DishRepository {
        return DishRepository(dishes.map { it.copy() })
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is DishRepository) return false

        if (other.dishes != dishes) return false

        return true
    }

    override fun hashCode(): Int {
        return dishes.hashCode()
    }

    override fun toString(): String {
        return "DishRepository(dishes=$dishes)"
    }

}