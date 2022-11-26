package de.unihannover.hci.menudetector.fragments

// Android
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.adapters.RecyclerViewDishAdapter
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel


/**
 * TODO:
 * - Total sum sticky footer
 * - Text-to-speech
 */
class OrderFragment : Fragment(R.layout.fragment_order) {

    private val viewModel by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        val order: List<Dish> = viewModel.order
        val recyclerViewAdapter = RecyclerViewDishAdapter(order)

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerViewAdapter

        viewModel.orderChanges.observe(viewLifecycleOwner) {
            recyclerViewAdapter.updateItems(it)
        }

        recyclerViewAdapter.clickListener = {
            // TODO: Navigate to dish details and pass dish as parameter
        }

        recyclerViewAdapter.incrementCountListener = {
            viewModel.updateDish(it.copy(quantity = it.quantity + 1))
            Snackbar.make(view, "Added one ${it.name} to order", Snackbar.LENGTH_SHORT).show()
        }

        recyclerViewAdapter.decrementCountListener = {
            viewModel.updateDish(it.copy(quantity = it.quantity - 1))
            Snackbar.make(view, "Removed one ${it.name} from order", Snackbar.LENGTH_SHORT).show()
        }
    }
}