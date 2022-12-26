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

class TranslateAndSpeak(private val sourceLanguage: Locale, private val targetLanguage: Locale) {

    private lateinit var tts: TextToSpeech
    private var translatorObject: Translator
    private var fromEnglishToSourcetranslatorObject: Translator

    init {
        // Create a translator
        val translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage.language)
            .setTargetLanguage(targetLanguage.language)
            .build()

        translatorObject = Translation.getClient(translatorOptions)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translatorObject.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // TODO
            }
            .addOnFailureListener {
                // TODO
            }


        // Create second  translator
        val translatorOptions2 = TranslatorOptions.Builder()
            .setSourceLanguage(Locale.US.language)
            .setTargetLanguage(sourceLanguage.language)
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
                tts.language = sourceLanguage
                tts.speak(textToTranslate, TextToSpeech.QUEUE_ADD, null, null)
            }
        }
    }

    suspend fun translate(text: String): String {
        return suspendCoroutine {
            translatorObject.translate(text)
                .addOnSuccessListener { translatedText ->
                    it.resume(translatedText)
                }
                .addOnFailureListener { exception ->
                    it.resumeWithException(exception)
                }
        }
    }
    suspend fun translatefromEnglishTosource(text: String): String {
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

    fun translateAndSpeak(context: Context, dishes: List<Dish>) {
        CoroutineScope(Dispatchers.IO).launch {
            var orderSentence = "I would like to have "
            for ((index, dish) in dishes.withIndex()) {
                if (index > 0) {
                    orderSentence += " and "
                }
                orderSentence += "${dish.quantity} of ${dish.name}" //get original Text of Dish
            }
            orderSentence += ". Thank you!"

            val translatedText = translatefromEnglishTosource(orderSentence)
            speak(context, translatedText)
        }
    }
}