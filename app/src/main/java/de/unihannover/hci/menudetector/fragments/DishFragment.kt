package de.unihannover.hci.menudetector.fragments

// Android

// Internal dependencies
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishDetails
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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

    private val args: DishFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)  // TODO: Async'

        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dish = args.dish!!

        updateViewState()

        CoroutineScope(Dispatchers.Default).launch {
            fetchDetails()
        }
    }

    private fun updateViewState() {
        view?.findViewById<TextView>(R.id.text_name)?.setText(dish.name)
        view?.findViewById<TextView>(R.id.text_price)?.setText(dish.price.toString() + " €")    // TODO: Adopt currency

        view?.findViewById<ImageView>(R.id.image_dish)?.setImageBitmap(dish.details?.bitmap)

        view?.findViewById<TextView>(R.id.text_description)?.setText(dish.details?.description)
        view?.findViewById<TextView>(R.id.text_weight)?.setText((dish.details?.weight ?: "").toString())

        var addButton: FloatingActionButton? = view?.findViewById<FloatingActionButton>(R.id.button_add)
        addButton?.setOnClickListener(null)
        addButton?.setOnClickListener {
            viewModel.updateDish(dish.copy(quantity = dish.quantity + 1)); // TODO: How to re-build efficiently?
        }
    }

    private fun fetchGET(urlSpec: String): String {
        // TODO: 4xx/5xx

        return URL(urlSpec).readText()
    }

    private fun fetchDetails() {
        Log.d("ASD", "asdasd")

        // TODO: Enhance wiki data parser(s)
        val wikiEntry: CharSequence = fetchGET("https://de.wikipedia.org/wiki/" + dish.name)
        var fetchedDescription: String? = """<p>((?!<\/p>)(\s|.))+<\/p>""".toRegex()
            .find(wikiEntry)?.value   // TODO: Improve precision
        if(fetchedDescription != null) {
            fetchedDescription = fetchedDescription
                .replace("""<\/?[^>]+>""".toRegex(), "")
        }

        val wikiMedia: CharSequence = fetchGET("https://commons.wikimedia.org/w/index.php?search=" + dish.name + "&title=Special:MediaSearch&go=Go&type=image")
        //var fetchedImageSource: String? = """<img class=\"sd-image\"((?!src=\")(\s|.))+src=\"[^"]+\"""".toRegex()
        //                                  .find(wikiMedia)?.value   // TODO: Improve precision
        var fetchedImageSource = "https://upload.wikimedia.org/wikipedia/commons/6/6f/5aday_spinach.jpg"    // MOCK

        var fetchedBitmap: Bitmap? = null
        if(fetchedImageSource != null) {
            val `in` = URL(fetchedImageSource).openStream()
            fetchedBitmap = BitmapFactory.decodeStream(`in`)
        }

        MainScope().launch {
            println(999)
            viewModel.updateDish(
                dish.copy(
                    details = DishDetails(
                        bitmap = fetchedBitmap,
                        description = fetchedDescription
                    )
                )
            )

            dish = viewModel.menu.find {
                it.id == dish.id
            }!!

            updateViewState()
        }
    }

}