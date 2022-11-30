package de.unihannover.hci.menudetector.fragments

// Android
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

// Google
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.adapters.RecyclerViewDishAdapter
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel


/**
 * TODO:
 * - Add navigation
 * - Update order based on interaction with list items
 * - When clicking on item, navigate to details with corresponding data:
 *
 *      private val args: DishFragmentArgs by navArgs()
 *      val dishExample = args.dish
 *      view?.findViewById<TextView>(R.id.text_target_name)?.text = dishExample?.name
 */
class MenuFragment : Fragment(R.layout.fragment_menu) {

    /* ATTRIBUTES */

    private lateinit var navController: NavController
    private val viewModel by activityViewModels<MainActivityViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var scanFab: FloatingActionButton


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
        val recyclerViewAdapter = RecyclerViewDishAdapter(menu)

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerViewAdapter
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        viewModel.menuChanges.observe(viewLifecycleOwner) {
            recyclerViewAdapter.updateItems(it)
        }

        recyclerViewAdapter.clickListener = {
            // TODO: Navigate to dish details and pass dish as parameter
            val action = MenuFragmentDirections.actionMenuFragmentToDishFragment(it)
            navController.navigate(action)
        }

        recyclerViewAdapter.incrementCountListener = {
            viewModel.updateDish(it.copy(quantity = it.quantity + 1))
        }

        recyclerViewAdapter.decrementCountListener = {
            if (it.quantity > 0) {
                viewModel.updateDish(it.copy(quantity = it.quantity - 1))
            } else {
                Snackbar.make(view, "Quantity cannot be lower than zero", Snackbar.LENGTH_SHORT)
                    .setAction("Dismiss") {}
                    .show()
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
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu -> {
                        navController.navigate(R.id.action_menuFragment_to_orderFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun bindViews(view: View) {
        this.recyclerView = view.findViewById(R.id.recycler_view)
        this.scanFab = view.findViewById(R.id.fab_scan)
    }

}
