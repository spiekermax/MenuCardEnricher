package de.unihannover.hci.menudetector.util

// Google
import com.google.mlkit.nl.translate.TranslateLanguage


class Constants {
    companion object {
        val SUPPORTED_LANGUAGES: List<String> = TranslateLanguage.getAllLanguages()
    }
}