package de.unihannover.hci.menudetector.util

// Google
import com.google.mlkit.nl.translate.TranslateLanguage


class Constants {
    companion object {
        const val SHARED_PREFERENCES_KEY: String = "SHARED_PREFERENCES"
        const val SHARED_PREFERENCES_CURRENCY_KEY: String = "CURRENCY"
        const val SHARED_PREFERENCES_LANGUAGE_KEY: String = "LANGUAGE"
        const val SHARED_PREFERENCES_WEIGHT_SYSTEM_KEY: String = "WEIGHT_SYSTEM"

        val SUPPORTED_LANGUAGES: List<String> = TranslateLanguage.getAllLanguages()
    }
}