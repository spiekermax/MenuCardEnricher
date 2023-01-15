package de.unihannover.hci.menudetector.viewmodels

// Android
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

// Internal dependencies
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishBuilder
import de.unihannover.hci.menudetector.models.DishRepository


private val MOCKED_DISHES: List<Dish> = listOf(
    // Willkommen im Restaurant “Aus aller Welt”
    DishBuilder("Minestrone", "Example entry", 6.49).build(),                        // Class A: One word
    DishBuilder("Griechischer Salat", "Example entry", 3.99).build(),                // Class B: Multiple Words
    DishBuilder("Flammkuchen Elsässer Art", "Example entry", 5.99).build(),
    DishBuilder("Currywurst", "Example entry", 6.96).build(),
    DishBuilder("Spaghetti Bolognese", "Example entry", 7.99).build(),
    DishBuilder("Schweinshaxe mit Sauerkraut", "Example entry", 13.99).build(),
    DishBuilder("Köfte-Spieß", "Example entry", 5.99).build(),                       // Class C: Rare compound word
    DishBuilder("Wiener Schnitzel", "Example entry", 12.49).build(),
    DishBuilder("Waffeln mit Sahne", "Example entry", 5.99).build(),
    DishBuilder("Tiramisu", "Example entry", 3.00).build(),
    DishBuilder("Crème Brûlée", "Example entry", 3.00).build(),                      // Class D: Word(s) with special charatcers
)

class MainActivityViewModel : ViewModel() {

    /* ATTRIBUTES */

    private val dishRepository: DishRepository = DishRepository(MOCKED_DISHES)

    val menu: List<Dish>
        get() = dishRepository.dishes

    val menuChanges: LiveData<List<Dish>>
        get() = dishRepository.dishChanges

    val order: List<Dish>
        get() = menu.filter {
            it.quantity > 0
        }

    val orderChanges: LiveData<List<Dish>>
        get() = Transformations.map(menuChanges) { menu ->
            menu.filter { it.quantity > 0 }
        }


    /* METHODS */

    fun addDish(dish: Dish) = dishRepository.addDish(dish)
    fun addDishes(dishes: List<Dish>) = dishRepository.addDishes(dishes)
    fun findDishById(id: String) = dishRepository.findDishById(id)
    fun watchDishById(id: String) = dishRepository.watchDishById(id)
    fun putDish(dish: Dish) = dishRepository.putDish(dish)
    fun updateDish(dish: Dish) = dishRepository.updateDish(dish)
    fun removeDish(dish: Dish) = dishRepository.removeDish(dish)

    fun removeAllDishesFromOrder() {
        for (dish in order) {
            this.updateDish(dish.copy(quantity = 0))
        }
    }

}