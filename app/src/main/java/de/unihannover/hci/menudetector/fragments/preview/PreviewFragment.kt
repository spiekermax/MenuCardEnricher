package de.unihannover.hci.menudetector.fragments.preview

// Android

// Google

// Internal dependencies
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.adapters.RecyclerViewDishAdapter
import de.unihannover.hci.menudetector.fragments.preview.utils.DecimalDigitsInputFilter
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import java.util.*


class PreviewFragment : Fragment(R.layout.fragment_preview) {

    private val viewModel by activityViewModels<MainActivityViewModel>()

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        val preview: List<Dish> = viewModel.preview
        val recyclerViewAdapter = RecyclerViewDishAdapter(
            preview,
            showQuantity = false,
            isQuantityEditable = false,
            isDishDeletableAndEditable = true,
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

        recyclerViewAdapter.deleteDishListener = {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirm Delete")
            builder.setMessage("Are you sure you want to delete this dish?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                viewModel.deleteDish(viewModel.preview.indexOf(it))
                dialog.cancel()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            val alert = builder.create()
            alert.show()
        }

        recyclerViewAdapter.editDishListener = {

            val builder = AlertDialog.Builder(activity)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_edit_dish, null)
            val dishName = dialogView.findViewById<EditText>(R.id.dish_name)
            val dishPrice = dialogView.findViewById<EditText>(R.id.dish_price)

            dishName.setText(it.name)
            dishPrice.setText(it.price.toString())

            dishPrice.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2))

            builder.setView(dialogView)
                .setTitle("Edit dish")
                .setPositiveButton("Save") { dialog, _ ->
                    viewModel.editDish(viewModel.preview.indexOf(it), dishName.text.toString(), dishPrice.text.toString().toDouble())
                    dialog.cancel()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.show()

            val positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)

            val editTexts = listOf(dishName, dishPrice)
            for (editText in editTexts) {
                editText.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        val etDishName = dishName.text.toString().trim()
                        val etDishPrice = dishPrice.text.toString().trim()

                        if(etDishName.isEmpty()){
                            dishName.error = "The name of a dish cannot be empty!"
                            positiveButton.isEnabled = false
                        } else if(etDishPrice.isEmpty()) {
                            dishPrice.error ="The price of a dish cannot be empty!"
                            positiveButton.isEnabled = false
                        } else {
                            positiveButton.isEnabled = true
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence, start: Int, count: Int, after: Int) {
                    }

                    override fun afterTextChanged(
                        s: Editable) {
                    }
                })
            }

        }
    }
}
