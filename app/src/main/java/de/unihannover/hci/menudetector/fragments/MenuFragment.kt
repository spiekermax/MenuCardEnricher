package de.unihannover.hci.menudetector.fragments

// Java

// Android

// Google

// Internal dependencies
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.adapters.RecyclerViewDishAdapter
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.services.TranslationService
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import java.util.*


class MenuFragment : Fragment(R.layout.fragment_menu) {

    /* ATTRIBUTES */

    private lateinit var navController: NavController
    private val viewModel by activityViewModels<MainActivityViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var scanFab: FloatingActionButton

    private lateinit var tts: TextToSpeech

    private lateinit var dishCount: TextView

    private val translationService: TranslationService by lazy {
        TranslationService(requireContext(), lifecycle)
    }


    /* LIFECYCLE */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindToolbarMenu()
        bindViews(view)

        val menu: List<Dish> = viewModel.menu
        val recyclerViewAdapter = RecyclerViewDishAdapter(
            menu,
            appLanguage = translationService.appLanguage,
            showImage = true
        )



        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerViewAdapter
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        viewModel.menuChanges.observe(viewLifecycleOwner) {
            recyclerViewAdapter.updateItems(it)
        }

        recyclerViewAdapter.clickListener = {
            val action = MenuFragmentDirections.actionMenuFragmentToDishFragment(it.id)
            navController.navigate(action)
        }

        recyclerViewAdapter.incrementCountListener = {
            viewModel.updateDish(it.copy(quantity = it.quantity + 1))
            updateDishCountInBadge(calculateDishCount(), dishCount)
        }

        recyclerViewAdapter.decrementCountListener = {
            if (it.quantity > 0) {
                viewModel.updateDish(it.copy(quantity = it.quantity - 1))
                updateDishCountInBadge(calculateDishCount(), dishCount)
            } else {
                Snackbar.make(view, "Quantity cannot be lower than zero", Snackbar.LENGTH_SHORT)
                    .setAction("Dismiss") {}
                    .show()
            }
        }

        recyclerViewAdapter.sayItListener = { it ->
            val dishName: CharSequence = it.name
            tts = TextToSpeech(requireContext()) { result ->
                Locale.US
                if (result == TextToSpeech.SUCCESS) {
                    tts.language = Locale(it.language ?: "en")
                    tts.speak(it.originalName, TextToSpeech.QUEUE_ADD, null, null)
                }
            }
        }

        scanFab.setOnClickListener {
            navController.navigate(R.id.action_menuFragment_to_scanPermissionsFragment)
        }
    }


    /* METHODS */

    private fun bindToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment, menu)

                val menuItem = menu.findItem(R.id.menu)
                val actionView = menuItem.actionView
                dishCount = actionView!!.findViewById(R.id.dish_count)

                updateDishCountInBadge(calculateDishCount(), dishCount)

                actionView.setOnClickListener {
                    navController.navigate(R.id.action_menuFragment_to_orderFragment)
                }

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu -> {
                        navController.navigate(R.id.action_menuFragment_to_orderFragment)
                        true
                    }
                    R.id.menu_info_button -> {
                        navController.navigate(R.id.action_menuFragment_to_menuInfo)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun updateDishCountInBadge(count: String, view: TextView) {
        if (count == "0") {
            view.visibility = View.GONE
        } else if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        }
        view.text = count
    }

    private fun bindViews(view: View) {
        this.recyclerView = view.findViewById(R.id.recycler_view)
        this.scanFab = view.findViewById(R.id.fab_scan)
    }

    private fun calculateDishCount(): String {
        var temporaryDishCount = 0
        for (dish in viewModel.order) {
            temporaryDishCount += dish.quantity
        }
        return temporaryDishCount.toString()
    }


}