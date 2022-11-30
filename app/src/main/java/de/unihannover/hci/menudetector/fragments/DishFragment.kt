package de.unihannover.hci.menudetector.fragments

// Android
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishBuilder
import de.unihannover.hci.menudetector.models.DishDetails

// TODO: Integrate actual button images

/**
 * TODO:
 * - Add dish details to incoming dishes without details field set
 * - Show image from dish details and other information
 * - Optional: Description
 */
class DishFragment : Fragment(R.layout.fragment_dish) {

    private lateinit var navController: NavController

    // TODO: Bind dynamically
    // TODO: Integrate image accordingly
    private var dish: Dish = DishBuilder("Nudelsuppe mit Huhn auf Sojabasis", 6.95)
    .details(DishDetails(
        description = "Neben Sushi ist Ramen die Nationalspeise Japans: Weizennudeln in einer beliebig nuancierten Brühe erlauben vielfältige Kreationen. Das Shoyu Ramen ist eine auf Soja basierende Brühe, welche sch durch ein salziges Rüstaroma auszeichnet. Das Gericht enthält ausßerdem zartes Hühnerfleisch, Rettich und Frühlingszwiebeln.",
    ))
    .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDish()

        view.findViewById<ImageButton>(R.id.button_back).setOnClickListener {
            navController.popBackStack()
        }
        view.findViewById<ImageButton>(R.id.button_order).setOnClickListener {
            navController.navigate(R.id.orderFragment)
        }
        view.findViewById<FloatingActionButton>(R.id.button_add).setOnClickListener {
            // TODO: Implement
            // it.animate().rotation(180F)
        }
    }


    fun initDish() {
        view?.findViewById<TextView>(R.id.text_description)?.setText(dish.details?.description)
        view?.findViewById<TextView>(R.id.text_weight)?.setText(dish.details?.weight.toString())
        view?.findViewById<TextView>(R.id.text_price)?.setText(dish.price.toString())
    }

}

