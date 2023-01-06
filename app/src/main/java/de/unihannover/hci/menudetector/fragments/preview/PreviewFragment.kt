package de.unihannover.hci.menudetector.fragments.preview

// Kotlin
import android.content.DialogInterface
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Android
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView

// Google
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.adapters.RecyclerViewDishAdapter
import de.unihannover.hci.menudetector.fragments.preview.utils.DecimalDigitsInputFilter
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishBuilder
import de.unihannover.hci.menudetector.models.recognition.DishRecognitionResult
import de.unihannover.hci.menudetector.models.recognition.MenuRecognitionResult
import de.unihannover.hci.menudetector.services.TranslationService
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel


class PreviewFragment : Fragment(R.layout.fragment_preview) {

    /* ATTRIBUTES */

    private val args: PreviewFragmentArgs by navArgs()
    private val viewModel by activityViewModels<MainActivityViewModel>()

    private val translationService by lazy {
        TranslationService(requireContext(), lifecycle)
    }

    private val _menu: MutableLiveData<List<Dish>?> = MutableLiveData(null)
    private var menu: List<Dish>?
        get() = _menu.value
        set(value) {
            _menu.postValue(value)
        }
    private val menuChanges: LiveData<List<Dish>?> = _menu


    /* LIFECYCLE */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recognizedMenu: MenuRecognitionResult = args.recognizedMenu ?: MenuRecognitionResult()
        lifecycleScope.launch {
            val translation = async { translateRecognizedMenu(recognizedMenu) }
            val delay = async { delay(1000) }

            val (translationResult) = awaitAll(translation, delay)
            val translatedMenu: List<Dish> = translationResult as List<Dish>

            menu = translatedMenu
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val recyclerViewAdapter = RecyclerViewDishAdapter(
            menu ?: listOf(),
            showQuantity = false,
            isQuantityEditable = false,
            isDishModifiable = true,
        )

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerViewAdapter

        val confirmButton: FloatingActionButton = view.findViewById(R.id.button_confirm)
        confirmButton.setOnClickListener {
            viewModel.addDishes(menu ?: listOf())
            menu = listOf()

            findNavController().navigate(R.id.action_previewFragment_to_menuFragment)
        }

        val loadingIndicator: View = view.findViewById(R.id.loading_indicator)

        menuChanges.observe(viewLifecycleOwner) {
            confirmButton.visibility = if (it == null) View.GONE else View.VISIBLE
            loadingIndicator.visibility = if (it == null) View.VISIBLE else View.GONE

            recyclerViewAdapter.updateItems(it ?: listOf())
        }

        recyclerViewAdapter.deleteDishListener = {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Confirm Delete")
            builder.setMessage("Are you sure you want to delete this dish?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                menu = (menu ?: listOf()).filter { dish -> dish.id != it.id }
                dialog.cancel()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            val alert = builder.create()
            alert.show()
        }

        recyclerViewAdapter.editDishListener = {
            val builder = AlertDialog.Builder(requireActivity())
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
                    menu = (menu ?: listOf()).map { dish ->
                        if (dish.id == it.id) {
                            DishBuilder(dishName.text.toString(), dishPrice.text.toString().toDouble()).id(dish.id).build()
                        } else {
                            dish
                        }
                    }
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


    /* METHODS */

    private suspend fun translateRecognizedMenu(recognizedMenu: MenuRecognitionResult): List<Dish> {
        val untranslatedDishes: List<DishRecognitionResult> = recognizedMenu.dishes

        return if (recognizedMenu.language == null) {
            untranslatedDishes.map {
                DishBuilder(it.name, it.price).build()
            }
        } else {
            untranslatedDishes.map {
                val convertedPrice = it.price
                val translatedName = translationService.translateIntoAppLanguage(
                    text = it.name,
                    sourceLanguage = recognizedMenu.language,
                )

                DishBuilder(translatedName, convertedPrice).build()
            }
        }
    }

}