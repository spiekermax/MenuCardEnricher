package de.unihannover.hci.menudetector.services

// Android
import android.content.Context
import android.content.SharedPreferences

// Internal dependencies
import de.unihannover.hci.menudetector.util.Constants


private const val EURO_IN_USD: Double = 1.08

class CurrencyService(val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE,
    )

    val appCurrency: String
        get() {
            return when (sharedPreferences.getString(
                Constants.SHARED_PREFERENCES_CURRENCY_KEY,
                null,
            )) {
                "Euro (€)" -> "EUR"
                "Dollar (\$)" -> "USD"
                else -> "EUR"
            }
        }

    fun convertIntoAppCurrency(value: Double, currency: String): Double {
        return convert(value, currency, appCurrency)
    }

    private fun convert(fromValue: Double, fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) return fromValue

        return if (fromCurrency == "EUR" && toCurrency == "USD") {
            fromValue * EURO_IN_USD
        } else if (fromCurrency == "USD" && toCurrency == "EUR") {
            fromValue / EURO_IN_USD
        } else {
            throw IllegalArgumentException("Unsupported currency.")
        }
    }

}