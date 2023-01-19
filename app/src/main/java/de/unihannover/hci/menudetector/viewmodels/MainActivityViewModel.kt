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
    DishBuilder("Minestrone", "Example entry", "EUR", 6.49).language("de").build(),                        // Class A: One word
    DishBuilder("Griechischer Salat", "Example entry", "EUR", 3.99).language("de").build(),                // Class B: Multiple Words
    DishBuilder("Flammkuchen Elsässer Art", "Example entry", "EUR", 5.99).language("de").build(),
    DishBuilder("Currywurst", "Example entry", "EUR", 6.96).language("de").build(),
    DishBuilder("Spaghetti Bolognese", "Example entry", "EUR", 7.99).language("de").build(),
    DishBuilder("Schweinshaxe mit Sauerkraut", "Example entry", "EUR", 13.99).language("de").build(),
    DishBuilder("Köfte-Spieß", "Example entry", "EUR", 5.99).language("de").build(),                       // Class C: Rare compound word
    DishBuilder("Wiener Schnitzel", "Example entry", "EUR", 12.49).language("de").build(),
    DishBuilder("Waffeln mit Sahne", "Example entry", "EUR", 5.99).language("de").build(),
    DishBuilder("Tiramisu", "Example entry", "EUR", 3.00).language("de").build(),
    DishBuilder("Crème Brûlée", "Example entry", "EUR", 3.00).language("de").build(),                      // Class D: Word(s) with special charatcers
)

class MainActivityViewModel : ViewModel() {

    /* ATTRIBUTES */

    private val dishRepository: DishRepository = DishRepository(listOf())

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