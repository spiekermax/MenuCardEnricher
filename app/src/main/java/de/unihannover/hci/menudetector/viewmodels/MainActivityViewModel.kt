package de.unihannover.hci.menudetector.viewmodels

// Android
import androidx.lifecycle.ViewModel

// Internal dependencies
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.Menu
import de.unihannover.hci.menudetector.models.Order
import de.unihannover.hci.menudetector.models.state.MainActivityState


class MainActivityViewModel : ViewModel() {
    val state: MainActivityState = MainActivityState(
        menu = Menu(
            listOf(
                Dish("Eis", 1.99, null),
                Dish("Nudeln", 3.99, null),
                Dish("Kartoffeln", 6.99, null),
                Dish("Brot", 2.99, null),
                Dish("Bohnen", 4.99, null),
                Dish("Spinat", 2.95, null),
                Dish("Schnitzel", 12.99, null),
                Dish("Pfannkuchen", 5.99, null),
                Dish("Suppe", 6.23, null),
                Dish("Currywurst", 6.96, null),
            )
        ),
        order = Order(mutableListOf())
    )
}