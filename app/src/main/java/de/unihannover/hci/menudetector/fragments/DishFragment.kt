package de.unihannover.hci.menudetector.fragments

// Android

// Internal dependencies
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishDetails
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import java.net.URL


/**
 * TODO:
 * - Add dish details to incoming dishes without details field set
 * - Show image from dish details and other information
 * - Optional: Description
 */
class DishFragment : Fragment(R.layout.fragment_dish) {

    private lateinit var navController: NavController
    private lateinit var dish: Dish

    private val viewModel by activityViewModels<MainActivityViewModel>()

    private fun fetchGET(urlSpec: String): String {
        return URL(urlSpec).readText()

        // TODO: 4xx/5xx
    }

    private fun fetchDetails(dish: Dish) {
        // TODO: Async?

        val wikiEntry: String = fetchGET("https://de.wikipedia.org/wiki/" + dish.name)
        var fetchedDescription: String? = """<p>((?!<\/p>)(\s|.))+<\/p>""".toRegex()
                                          .find(wikiEntry)?.value   // TODO: Improve precision
        if(fetchedDescription != null) {
            fetchedDescription = fetchedDescription
                                 .replace("""<\/?[^>]+>""".toRegex(), "")
        }

        var fetchedBitmap: Bitmap?
        val wikiMedia: String = fetchGET("https://commons.wikimedia.org/w/index.php?search=" + dish.name + "&title=Special:MediaSearch&go=Go&type=image")
        //var fetchedImageSource: String? = """<img class=\"sd-image\"((?!src=\")(\s|.))+src=\"[^"]+\"""".toRegex()
        //                                  .find(wikiMedia)?.value   // TODO: Improve precision
        var fetchedImageSource = "https://upload.wikimedia.org/wikipedia/commons/6/6f/5aday_spinach.jpg"    // MOCK

        if(fetchedImageSource == null) {
            // TODO: Handle fetch error
            return
        }

        val `in` = URL(fetchedImageSource).openStream()

        fetchedBitmap = BitmapFactory.decodeStream(`in`)

        dish.details = DishDetails(
            bitmap = fetchedBitmap,
            description = fetchedDescription
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)  // TODO: Async'

        navController = findNavController()
    }

    override fun onResume() {
        super.onResume()

        dish = viewModel.menu.get(5)    // TODO: Get passed index reference

        if(dish.details == null) {
            fetchDetails(dish)

            viewModel.updateDish(dish);
        }

        view?.findViewById<TextView>(R.id.text_name)?.setText(dish.name)
        view?.findViewById<TextView>(R.id.text_price)?.setText(dish.price.toString() + " €")    // TODO: Adopt currency

        view?.findViewById<ImageView>(R.id.image_dish)?.setImageBitmap(dish.details?.bitmap)

        view?.findViewById<TextView>(R.id.text_description)?.setText(dish.details?.description)
        view?.findViewById<TextView>(R.id.text_weight)?.setText((dish.details?.weight ?: "").toString())

        var addButton: FloatingActionButton? = view?.findViewById<FloatingActionButton>(R.id.button_add)
        addButton?.setOnClickListener(null)
        addButton?.setOnClickListener {
            dish.quantity++

            viewModel.updateDish(dish); // TODO: How to re-build efficiently?
        }
    }

}