package de.unihannover.hci.menudetector.fragments

// Android
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.button.MaterialButton

// Google
import com.google.android.material.snackbar.Snackbar

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.adapters.RecyclerViewDishAdapter
import de.unihannover.hci.menudetector.services.TranslateAndSpeak
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.services.CurrencyService
import de.unihannover.hci.menudetector.services.TranslationService
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import java.text.NumberFormat
import java.util.*


/**
 * TODO:
 * - Total sum sticky footer
 * - Text-to-speech
 */
class OrderFragment : Fragment(R.layout.fragment_order) {

    private val viewModel by activityViewModels<MainActivityViewModel>()
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var navController: NavController
    private val tas: TranslateAndSpeak by lazy {
        val targetLang = viewModel.order.groupingBy {
            it.language
        }.eachCount().maxBy { it.value }.key

        TranslateAndSpeak(Locale(targetLang ?: "en"))
    }

    private val translationService: TranslationService by lazy {
        TranslationService(requireContext(), lifecycle)
    }

    private val currencyService: CurrencyService by lazy {
        CurrencyService(requireContext())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val totalOrder: TextView = view.findViewById(R.id.text_total)
        bindToolbarMenu()
        sharedPreferences = activity?.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)!!
        val targetLanguageIndex = sharedPreferences.getInt("LANGUAGE", 0)

        val order: List<Dish> = viewModel.order
        val recyclerViewAdapter = RecyclerViewDishAdapter(order, appLanguage = translationService.appLanguage)
        val locales: List<Locale> = Locale.getAvailableLocales().asList().distinctBy { it.language }
        val sayItButton: MaterialButton = view.findViewById(R.id.button_say_it)


        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        if (order.isEmpty()) {
            sayItButton.isEnabled = false
            sayItButton.isClickable = false
        }

        totalOrder.text = calculateTotal()

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerViewAdapter
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        viewModel.orderChanges.observe(viewLifecycleOwner) {
            recyclerViewAdapter.updateItems(it)
        }

        recyclerViewAdapter.clickListener = {
            val action = OrderFragmentDirections.actionOrderFragmentToDishFragment(it.id)
            navController.navigate(action)
        }

        recyclerViewAdapter.incrementCountListener = {
            val totalOrder: TextView = view.findViewById(R.id.text_total)
            viewModel.updateDish(it.copy(quantity = it.quantity + 1))
            totalOrder.text = calculateTotal()
            if (viewModel.order.isEmpty()) {
                sayItButton.isEnabled = false
                sayItButton.isClickable = false
            }

        }

        recyclerViewAdapter.decrementCountListener = {
            val totalOrder: TextView = view.findViewById(R.id.text_total)
            if (it.quantity > 0) {
                viewModel.updateDish(it.copy(quantity = it.quantity - 1))
                totalOrder.text = calculateTotal()
                if (viewModel.order.isEmpty()) {
                    sayItButton.isEnabled = false
                    sayItButton.isClickable = false
                }
            } else {
                Snackbar.make(view, "Quantity cannot be lower than zero", Snackbar.LENGTH_SHORT)
                    .setAction("Dismiss") {}
                    .show()
                totalOrder.text = calculateTotal()
                if (viewModel.order.isEmpty()) {
                    sayItButton.isEnabled = false
                    sayItButton.isClickable = false
                }
            }
        }

        recyclerViewAdapter.sayItListener = {
            tas.speak(requireContext(), it.originalName)
        }

        sayItButton.setOnClickListener {
            tas.readOutOrder(requireContext(), viewModel.order)
        }
    }


    /* METHODS */

    private fun bindToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.order_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.help -> {
                        navController.navigate(R.id.action_orderFragment_to_orderInfo)
                        true
                    }
                    R.id.clear -> {
                        viewModel.removeAllDishesFromOrder()
                        val totalOrder: TextView = requireView().findViewById(R.id.text_total)
                        totalOrder.text = calculateTotal()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun calculateTotal(): String {
        var totalSum = 0.0
        for (dish in viewModel.order) {
            totalSum += (dish.quantity * dish.price)
        }
        return "Total: " + formatPrice(totalSum, currencyService.appCurrency, translationService.appLanguage)
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