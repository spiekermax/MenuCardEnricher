package de.unihannover.hci.menudetector.services

import android.content.Context
import android.speech.tts.TextToSpeech
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import de.unihannover.hci.menudetector.models.Dish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TranslateAndSpeak(private val targetLanguage: Locale) {

    private lateinit var tts: TextToSpeech
    private var fromEnglishToSourcetranslatorObject: Translator

    init {
        // Create second  translator
        val translatorOptions2 = TranslatorOptions.Builder()
            .setSourceLanguage(Locale.US.language)
            .setTargetLanguage(targetLanguage.language)
            .build()

        fromEnglishToSourcetranslatorObject = Translation.getClient(translatorOptions2)

        val conditions2 = DownloadConditions.Builder()
            .requireWifi()
            .build()

        fromEnglishToSourcetranslatorObject.downloadModelIfNeeded(conditions2)
            .addOnSuccessListener {
                // TODO
            }
            .addOnFailureListener {
                // TODO
            }
    }

    fun speak(context: Context, textToTranslate: String) {
        tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = targetLanguage
                tts.speak(textToTranslate, TextToSpeech.QUEUE_ADD, null, null)
            }
        }
    }

    private suspend fun translateFromEnglishToLanguage(text: String): String {
        return suspendCoroutine {
            fromEnglishToSourcetranslatorObject.translate(text)
                .addOnSuccessListener { translatedText ->
                    it.resume(translatedText)
                }
                .addOnFailureListener { exception ->
                    it.resumeWithException(exception)
                }
        }
    }

    fun readOutOrder(context: Context, dishes: List<Dish>) {
        CoroutineScope(Dispatchers.IO).launch {
            var orderSentence = "I would like to have "
            for ((index, dish) in dishes.withIndex()) {
                if (index > 0) {
                    orderSentence += " and "
                }
                orderSentence += "${dish.quantity} of ${dish.originalName.lowercase()}" //get original Text of Dish
            }
            orderSentence += ". Thank you!"

            val translatedText = translateFromEnglishToLanguage(orderSentence)
            speak(context, translatedText)
        }
    }
}