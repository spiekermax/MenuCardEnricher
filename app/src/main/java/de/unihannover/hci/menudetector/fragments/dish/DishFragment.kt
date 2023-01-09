package de.unihannover.hci.menudetector.fragments.dish

// Android

// Internal dependencies
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import de.unihannover.hci.menudetector.MainActivity
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishDetails
import de.unihannover.hci.menudetector.util.formatPrice
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * TODO:
 * - Add dish details to incoming dishes without details field set
 * - Show dish details and other information
 * - Description
 * - Optional: Image
 */
class DishFragment : Fragment(R.layout.fragment_dish) {

    private lateinit var navController: NavController
    private lateinit var dish: Dish

    private var currentQuantity: Int = 0

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

        dish = viewModel.findDishById(args.dishID) ?: throw RuntimeException("Null")

        currentQuantity = dish.quantity

        updateViewState()

        if(dish.details !== null) return

        CoroutineScope(Dispatchers.Default).launch {
            var details: DishDetails?
            try {
                details = DetailsRetrieval.fetch(dish)
            } catch(e: Exception) {
                MainScope().launch {
                    Toast.makeText(activity?.applicationContext, "Dish details could not be parsed", Toast.LENGTH_LONG).show()
                    // TODO: Specify error reason (No network, Bad source, ...)

                    navController.popBackStack();
                }

                return@launch
            }

            MainScope().launch {
                viewModel.updateDish(
                    dish.copy(details = details)
                )

                dish = viewModel.menu.find {
                    it.id == dish.id
                }!!

                updateViewState()
            }
        }
    }

    private fun updateViewState() {
        if(dish.details === null) {
            view?.findViewById<ProgressBar>(R.id.progressBar)?.setVisibility(View.VISIBLE)

            return
        }

        view?.findViewById<ProgressBar>(R.id.progressBar)?.setVisibility(View.GONE)

        view?.findViewById<TextView>(R.id.text_name)?.setText(dish.name)
        view?.findViewById<TextView>(R.id.text_price)?.setText(formatPrice(dish.price))    // TODO: Adopt currency

        var addButton: FloatingActionButton? = view?.findViewById(R.id.button_add)
        addButton?.setOnClickListener(null)
        addButton?.setOnClickListener {
            currentQuantity++

            viewModel.updateDish(dish.copy(quantity = currentQuantity));
        }

        val imageView: ImageView? = view?.findViewById(R.id.image_dish)
        if(dish.details?.bitmap !== null) {
            imageView?.setImageBitmap(dish.details?.bitmap)
            imageView?.setVisibility(View.VISIBLE)
        } else {
            imageView?.setVisibility(View.GONE)
        }

        view?.findViewById<TextView>(R.id.text_description)?.setText(dish.details?.description)
        view?.findViewById<TextView>(R.id.text_weight)?.setText((dish.details?.weight ?: "").toString())
    }

}