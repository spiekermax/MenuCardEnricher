package de.unihannover.hci.menudetector.viewmodels

// Android
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

// Internal dependencies
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishBuilder
import de.unihannover.hci.menudetector.models.DishRepository


private val MOCKED_DISHES: List<Dish> = listOf(
    // Willkommen im Restaurant “Aus aller Welt”
    DishBuilder("Minestrone", 6.49).build(),                        // Class A: One word
    DishBuilder("Griechischer Salat", 3.99).build(),                // Class B: Multiple Words
    DishBuilder("Flammkuchen Elsässer Art", 5.99).build(),
    DishBuilder("Currywurst", 6.96).build(),
    DishBuilder("Spaghetti Bolognese", 7.99).build(),
    DishBuilder("Eisbein mit Sauerkraut", 13.99).build(),
    DishBuilder("Köfte-Spieß", 5.99).build(),                       // Class C: Rare compound word
    DishBuilder("Rumpsteak mit Steakhouse Fries", 4.99).build(),    // TODO: Multiple component info for manifold combination dishes?
    DishBuilder("Wiener Schnitzel", 12.49).build(),
    DishBuilder("Waffeln mit Sahne", 5.99).build(),
    DishBuilder("Tiramisu", 3.00).build(),
    DishBuilder("Crème Brûlée", 3.00).build(),                      // Class D: Word(s) with special charatcers
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

    private val _preview: MutableLiveData<List<Dish>> = MutableLiveData(listOf())

    var preview: List<Dish>
        get() = _preview.value!!
        set(value) {
            _preview.value = value
        }

    val previewChanges: LiveData<List<Dish>> = _preview


    /* METHODS */

    fun addDish(dish: Dish) = dishRepository.addDish(dish)
    fun addDishes(dishes: List<Dish>) = dishRepository.addDishes(dishes)
    fun findDishById(id: String) = dishRepository.findDishById(id)
    fun watchDishById(id: String) = dishRepository.watchDishById(id)
    fun putDish(dish: Dish) = dishRepository.putDish(dish)
    fun updateDish(dish: Dish) = dishRepository.updateDish(dish)
    fun removeDish(dish: Dish) = dishRepository.removeDish(dish)

    fun approvePreview() {
        addDishes(preview)
        preview = listOf()
    }

}