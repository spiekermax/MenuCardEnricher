package de.unihannover.hci.menudetector.fragments

// Android
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.button.MaterialButton

// Google
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.adapters.RecyclerViewDishAdapter
import de.unihannover.hci.menudetector.analyzer.TranslateAndSpeak
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import java.util.*


/**
 * TODO:
 * - Total sum sticky footer
 * - Text-to-speech
 */
class OrderFragment : Fragment(R.layout.fragment_order) {

    private val viewModel by activityViewModels<MainActivityViewModel>()
    private lateinit var tts : TextToSpeech

    private lateinit var translatorObject: Translator
    private  lateinit var tas : TranslateAndSpeak

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sayItButton: MaterialButton = view.findViewById(R.id.button_say_it)


        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        tas = TranslateAndSpeak(Locale.US,Locale.GERMAN)

        val order: List<Dish> = viewModel.order
        val recyclerViewAdapter = RecyclerViewDishAdapter(order)
        val totalOrder: TextView= view.findViewById(R.id.text_total)
        totalOrder.text = calculateTotal()

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerViewAdapter
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        viewModel.orderChanges.observe(viewLifecycleOwner) {
            recyclerViewAdapter.updateItems(it)
        }

        recyclerViewAdapter.clickListener = {
            // TODO: Navigate to dish details and pass dish as parameter
        }

        recyclerViewAdapter.incrementCountListener = {
            val totalOrder: TextView= view.findViewById(R.id.text_total)
            viewModel.updateDish(it.copy(quantity = it.quantity + 1))
            totalOrder.text = calculateTotal()
        }

        recyclerViewAdapter.decrementCountListener = {
            val totalOrder: TextView= view.findViewById(R.id.text_total)
            if (it.quantity > 0) {
                viewModel.updateDish(it.copy(quantity = it.quantity - 1))
                totalOrder.text = calculateTotal()
            } else {
                Snackbar.make(view, "Quantity cannot be lower than zero", Snackbar.LENGTH_SHORT)
                    .setAction("Dismiss") {}
                    .show()
                totalOrder.text = calculateTotal()
            }
        }

        recyclerViewAdapter.sayItListener = {
            val dishName = it.name
            tas.speak(requireContext(),dishName)
        }

        sayItButton.setOnClickListener {
            tas.translateAndSpeak(requireContext(), viewModel.order)
        }
    }

     private fun calculateTotal(): String {
        var totalSum = 0.0
        for (dish in viewModel.order){
            totalSum+= (dish.quantity * dish.price)
        }
        return "Total: "+ String.format("%.2f", totalSum) + "€"

    }
}