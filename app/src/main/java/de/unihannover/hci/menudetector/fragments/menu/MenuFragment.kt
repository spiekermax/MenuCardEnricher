package de.unihannover.hci.menudetector.fragments.menu

// Android
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView


// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.fragments.menu.adapter.MenuItemAdapter
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var navController: NavController
    private val viewModel by activityViewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Daten initialisieren

        val dishList = viewModel.state.menu!!.dishes

        val addMenuSitefab: View = view.findViewById(R.id.addSite)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val menuAdapter = MenuItemAdapter(dishList)

        var dishCountStr: String
        var dishCountInt: Int

        recyclerView.adapter = menuAdapter

        menuAdapter.itemClickListener = {
            Toast.makeText(requireActivity(), "Show details of dish " + it.name, Toast.LENGTH_SHORT)
                .show()
        }

        menuAdapter.addToOrdersClickListener = { dishCount, dish ->
            dishCountStr = dishCount.text.toString()
            dishCountInt = dishCountStr.toInt()
            dishCountInt++
            dishCount.text = dishCountInt.toString()
            Toast.makeText(
                requireActivity(),
                "Add dish " + dish.name + " to order/ Count: " + dishCountInt,
                Toast.LENGTH_SHORT
            ).show()
        }

        menuAdapter.removeFromOrdersClickListener = { dishCount, dish ->
            dishCountStr = dishCount.text.toString()
            dishCountInt = dishCountStr.toInt()
            if (dishCountInt > 0) {
                dishCountInt--
                dishCount.text = dishCountInt.toString()
            }
            Toast.makeText(
                requireActivity(),
                "Remove dish " + dish.name + " from order/ Count: " + dishCountInt,
                Toast.LENGTH_SHORT
            ).show()
        }

        addMenuSitefab.setOnClickListener {
            Toast.makeText(requireActivity(), "Move to Scan Screen", Toast.LENGTH_SHORT).show()
        }

        recyclerView.setHasFixedSize(true)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_menu_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu -> {
                        Toast.makeText(requireActivity(), "Order", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

}
