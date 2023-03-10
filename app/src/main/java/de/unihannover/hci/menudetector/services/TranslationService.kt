package de.unihannover.hci.menudetector.services

// Java
import java.util.Locale

// Kotlin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Android
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Lifecycle

// Google
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

// Internal dependencies
import de.unihannover.hci.menudetector.util.await
import de.unihannover.hci.menudetector.util.Constants


class TranslationService(val context: Context, private val lifecycle: Lifecycle) {

    /* COMPANION */

    private companion object {
        fun isLanguageSupported(language: String): Boolean {
            return Constants.SUPPORTED_LANGUAGES.contains(language)
        }
    }


    /* ATTRIBUTES */

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE,
    )

    val appLanguage: String
        get() {
            val preferredLanguage: String? = sharedPreferences.getString(Constants.SHARED_PREFERENCES_LANGUAGE_KEY, null)

            return if (preferredLanguage != null) {
                preferredLanguage
            } else {
                val deviceLanguage: String = Locale.getDefault().language
                if (isLanguageSupported(deviceLanguage)) {
                    deviceLanguage
                } else {
                    Constants.SUPPORTED_LANGUAGES[0]
                }
            }
        }

    private val translators: MutableMap<String, Translator> = mutableMapOf()


    /* METHODS */

    suspend fun translateIntoAppLanguage(text: String, sourceLanguage: String): String {
        return translate(text, sourceLanguage, appLanguage)
    }

    suspend fun translateIntoGerman(text: String, sourceLanguage: String): String {
        return translate(text, sourceLanguage, TranslateLanguage.GERMAN)
    }

    private suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String,
    ): String {
        if (!isLanguageSupported(sourceLanguage)) throw IllegalArgumentException("Language '$sourceLanguage' is not supported")
        if (!isLanguageSupported(targetLanguage)) throw IllegalArgumentException("Language '$targetLanguage' is not supported")

        val translator: Translator
        val translatorKey = "$sourceLanguage$targetLanguage"

        if (translators.containsKey(translatorKey)) {
            translator = translators[translatorKey]!!
        } else {
            val translatorOptions = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()

            translator = Translation.getClient(translatorOptions)
            translator.downloadModelIfNeeded().await()

            withContext(Dispatchers.Main) {
                lifecycle.addObserver(translator)
            }

            translators[translatorKey] = translator
        }

        return translator.translate(text).await()
    }

}