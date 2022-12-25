package de.unihannover.hci.menudetector.services

// Java
import java.util.Locale

// Kotlin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Android
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle

// Google
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

// Internal dependencies
import de.unihannover.hci.menudetector.util.await


private const val SHARED_PREFERENCES_KEY: String = "SHARED_PREF"
private const val SHARED_PREFERENCES_LANGUAGE_KEY: String = "LANGUAGE"

class TranslationService(val context: Context, private val lifecycle: Lifecycle) {

    /* ATTRIBUTES */

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE,
    )

    private val languages: List<String> =
        Locale.getAvailableLocales().map { it.language }.distinct()

    private val appLanguage: String
        get() {
            val index: Int = sharedPreferences.getInt(SHARED_PREFERENCES_LANGUAGE_KEY, -1)
            if (index == -1) {
                return Locale.getDefault().language
            } else {
                return languages[index]
            }
        }

    private val translators: MutableMap<String, Translator> = mutableMapOf()


    /* METHODS */

    suspend fun translateIntoAppLanguage(text: String, sourceLanguage: String): String {
        return translate(text, sourceLanguage, appLanguage)
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


    /* UTILITY */

    fun isLanguageSupported(language: String): Boolean {
        return languages.contains(language)
    }
}