package de.unihannover.hci.menudetector.fragments

// Android
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

// Google
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.adapters.RecyclerViewDishAdapter
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel


class PreviewFragment : Fragment(R.layout.fragment_preview) {

    private val viewModel by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        val preview: List<Dish> = viewModel.preview
        val recyclerViewAdapter = RecyclerViewDishAdapter(
            preview,
            showQuantity = false,
            isQuantityEditable = false,
        )

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerViewAdapter

        viewModel.previewChanges.observe(viewLifecycleOwner) {
            recyclerViewAdapter.updateItems(it)
        }

        val confirmButton: FloatingActionButton = view.findViewById(R.id.button_confirm)
        confirmButton.setOnClickListener {
            viewModel.approvePreview()
            findNavController().navigate(R.id.action_previewFragment_to_menuFragment)
        }
    }

}