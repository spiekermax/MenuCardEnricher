package de.unihannover.hci.menudetector.fragments.dish

// Android

// Internal dependencies
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.models.DishDetails
import de.unihannover.hci.menudetector.services.TranslationService
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*


/**
 * TODO:
 * - Add dish details to incoming dishes without details field set
 * - Show dish details and other information
 * - Description
 * - Optional: Image
 */
class DishFragment : Fragment(R.layout.fragment_dish) {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<MainActivityViewModel>()

    private val args: DishFragmentArgs by navArgs()

    private val translationService: TranslationService by lazy {
        TranslationService(requireContext(), lifecycle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)  // TODO: Async'

        navController = findNavController()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.watchDishById(args.dishID).observe(viewLifecycleOwner) {
            if (it == null) throw RuntimeException("Dish may not be null")

            (activity as AppCompatActivity?)!!.supportActionBar!!.title = it.name

            if (it.details === null) {
                view?.findViewById<ProgressBar>(R.id.progressBar)?.setVisibility(View.VISIBLE)
                view?.findViewById<View>(R.id.footer)?.visibility = View.GONE
                return@observe
            }

            view?.findViewById<ProgressBar>(R.id.progressBar)?.setVisibility(View.GONE)
            view.findViewById<View>(R.id.footer)?.visibility = View.VISIBLE

            view.findViewById<TextView>(R.id.text_quantity)?.setText(it.quantity.toString())
            view?.findViewById<TextView>(R.id.text_name)?.setText(it.name)
            view.findViewById<TextView>(R.id.text_original_name)?.text = it.originalName
            view?.findViewById<TextView>(R.id.text_price)
                ?.setText(
                    formatPrice(
                        it.price,
                        it.currency,
                        translationService.appLanguage
                    )
                )

            CoroutineScope(Dispatchers.Main).launch {
                val image = view.findViewById<ImageView>(R.id.image_dish) ?: return@launch

                val imageUrl = DetailsRetrieval.fetchImageUrl(it)

                Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(image)
            }

            view?.findViewById<TextView>(R.id.text_description)?.setText(it.details?.description)
        }

        bindListeners()

        if (viewModel.findDishById(args.dishID)?.details !== null) return

        CoroutineScope(Dispatchers.IO).launch {
            var details: DishDetails?
            try {
                details = DetailsRetrieval.fetch(requireContext(), lifecycle, viewModel.findDishById(args.dishID)!!)
            } catch (e: Exception) {
                MainScope().launch {
                    Snackbar.make(
                        requireActivity().findViewById<View>(android.R.id.content).rootView,
                        "Could not find additional dish information",
                        Snackbar.LENGTH_LONG
                    ).show()
                    // TODO: Specify error reason (No network, Bad source, ...)

                    navController.popBackStack();
                }

                return@launch
            }

            MainScope().launch {
                val dish = viewModel.findDishById(args.dishID) ?: return@launch
                viewModel.updateDish(
                    dish.copy(details = details)
                )
            }
        }
    }

    private fun bindListeners() {
        val addButton: ImageButton? = view?.findViewById(R.id.button_increment_count)
        addButton?.setOnClickListener {
            val dish = viewModel.findDishById(args.dishID) ?: return@setOnClickListener
            viewModel.updateDish(dish.copy(quantity = dish.quantity + 1));
        }

        val removeButton: ImageButton? = view?.findViewById(R.id.button_decrement_count)
        removeButton?.setOnClickListener {
            val dish = viewModel.findDishById(args.dishID) ?: return@setOnClickListener

            if (dish.quantity > 0) {
                viewModel.updateDish(dish.copy(quantity = dish.quantity - 1));
            } else {
                Snackbar.make(
                    requireView(),
                    "Quantity cannot be lower than zero",
                    Snackbar.LENGTH_SHORT
                )
                    .setAction("Dismiss") {}
                    .show()
            }
        }
    }

    private fun formatPrice(price: Double, currency: String, language: String?): String {
        val locale: Locale? = if (language != null) Locale(language) else null

        val formatter = if (locale != null) {
            NumberFormat.getCurrencyInstance(locale)
        } else {
            NumberFormat.getCurrencyInstance()
        }

        formatter.currency = Currency.getInstance(currency)

        return formatter.format(price)
    }

}