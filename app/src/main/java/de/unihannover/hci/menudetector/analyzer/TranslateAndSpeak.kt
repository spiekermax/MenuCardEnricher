package de.unihannover.hci.menudetector.analyzer

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.fragment.app.activityViewModels
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import java.util.*

class TranslateAndSpeak(sourceLanguage:Locale, targetLanguage:Locale) {
    val sourceLanguage = sourceLanguage
    val targetLanguage= targetLanguage

    private lateinit var tts : TextToSpeech
    private lateinit var translatorObject: Translator

    fun speak(context:Context, textToTranslate: String){
        tts = TextToSpeech(context, TextToSpeech.OnInitListener {
            if(it == TextToSpeech.SUCCESS){
                tts.language = targetLanguage
                tts.setSpeechRate(1.0f)
                tts.speak(textToTranslate, TextToSpeech.QUEUE_ADD, null)
            } })
    }

    fun translateAndSpeak(context:Context, viewModel:MainActivityViewModel ){
        var OrderSentence ="I would like to have "
        for ((index, dish) in viewModel.order.withIndex()){
            if(index > 0){
                OrderSentence+=" and "
            }
            OrderSentence += dish.quantity.toString() + " of " + dish.name
        }
        OrderSentence += ". Thank you !!"
        // Create a translator:
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage.getLanguage())
            .setTargetLanguage(targetLanguage.getLanguage())
            .build()

        translatorObject = Translation.getClient(options)
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translatorObject.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...trans

            }

        translatorObject.translate(OrderSentence)
            .addOnSuccessListener { translatedText ->
                this.speak(context , translatedText)
            }
            .addOnFailureListener { exception ->
                // Error.
                // ...
            }
    }
}