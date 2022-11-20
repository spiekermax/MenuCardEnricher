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
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView


// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.fragments.menu.adapter.MenuItemAdapter
import de.unihannover.hci.menudetector.models.Dish

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Daten initialisieren

        val d0 =  Dish("Eis", 1.99, null)
        val d1 =  Dish("Nudeln", 3.99, null)
        val d2 =  Dish("Kartoffeln", 6.99, null)
        val d3 =  Dish("Brot", 2.99, null)
        val d4 =  Dish("Bohnen", 4.99, null)
        val d5 =  Dish("Spinat", 2.95, null)
        val d6 =  Dish("Schnitzel", 12.99, null)
        val d7 =  Dish("Pfannkuchen", 5.99, null)
        val d8 =  Dish("Suppe", 6.23, null)
        val d9 =  Dish("Currywurst", 6.96, null)

        val dishList = listOf(d0, d1, d2, d3, d4, d5, d6, d7, d8, d9)

        val addMenuSitefab: View = view.findViewById(R.id.addSite)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val menuAdapter = MenuItemAdapter(dishList)

        recyclerView.adapter = menuAdapter

        menuAdapter.itemClickListener = {
            Toast.makeText(requireActivity(), "Show details of dish " + it.name,Toast.LENGTH_SHORT).show()
        }

        menuAdapter.addToOrdersClickListener = {
            Toast.makeText(requireActivity(), "Add dish " + it.name + " to order",Toast.LENGTH_SHORT).show()
        }

        addMenuSitefab.setOnClickListener {
            Toast.makeText(requireActivity(), "Move to Scan Screen",Toast.LENGTH_SHORT).show()
        }

        recyclerView.setHasFixedSize(true)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_menu_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu -> {
                        Toast.makeText(requireActivity(), "Move to order",Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

}
