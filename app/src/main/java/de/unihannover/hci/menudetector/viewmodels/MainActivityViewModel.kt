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
    DishBuilder("Eis", 1.99).build(),
    DishBuilder("Nudeln", 3.99).build(),
    DishBuilder("Kartoffeln", 6.99).build(),
    DishBuilder("Brot", 2.99).build(),
    DishBuilder("Bohnen", 4.99).build(),
    DishBuilder("Spinat", 2.95).build(),
    DishBuilder("Schnitzel", 12.99).build(),
    DishBuilder("Pfannkuchen", 5.99).build(),
    DishBuilder("Suppe", 6.23).build(),
    DishBuilder("Currywurst", 6.96).build(),
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

    var preview: List<Dish> = listOf()


    /* METHODS */

    fun addDish(dish: Dish) = dishRepository.addDish(dish)
    fun putDish(dish: Dish) = dishRepository.putDish(dish)
    fun updateDish(dish: Dish) = dishRepository.updateDish(dish)
    fun removeDish(dish: Dish) = dishRepository.removeDish(dish)

}