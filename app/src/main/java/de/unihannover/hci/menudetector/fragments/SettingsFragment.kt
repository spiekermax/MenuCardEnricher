package de.unihannover.hci.menudetector.fragments

// Java
import java.util.Locale

// Android
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment

// Google
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.fragments.info.SettingsInfo
import de.unihannover.hci.menudetector.util.Constants


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    /* ATTRIBUTES */

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE,
        )
    }

    private val sharedPreferencesEditor: SharedPreferences.Editor by lazy {
        sharedPreferences.edit()
    }

    private val supportedLanguages: List<String>
        get() = Constants.SUPPORTED_LANGUAGES

    private val supportedCurrencies: List<String>
        get() = resources.getStringArray(R.array.currencies).toList()

    private val supportedWeightSystems: List<String>
        get() = resources.getStringArray(R.array.weight_systems).toList()


    /* LIFECYCLE */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deviceLanguage: String = Locale.getDefault().language

        val defaultLanguage: String =
            if (supportedLanguages.contains(deviceLanguage)) {
                deviceLanguage
            } else {
                supportedLanguages[0]
            }
        val preferredLanguage: String = sharedPreferences.getString(
            Constants.SHARED_PREFERENCES_LANGUAGE_KEY,
            defaultLanguage,
        )!!

        val defaultCurrency: String = supportedCurrencies[0]
        val preferredCurrency: String = sharedPreferences.getString(
            Constants.SHARED_PREFERENCES_CURRENCY_KEY,
            defaultCurrency,
        )!!

        val defaultWeightSystem: String = supportedWeightSystems[0]
        val preferredWeightSystem: String = sharedPreferences.getString(
            Constants.SHARED_PREFERENCES_WEIGHT_SYSTEM_KEY,
            defaultWeightSystem,
        )!!

        val languageDropdown: AutoCompleteTextView = view.findViewById(R.id.language)
        val currencyDropdown: AutoCompleteTextView = view.findViewById(R.id.currency)
        val weightSystemDropdown: AutoCompleteTextView = view.findViewById(R.id.weight_system)

        val languageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            supportedLanguages.map {
                Locale(it).displayLanguage
            },
        )
        val currencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            supportedCurrencies,
        )
        val weightSystemAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            supportedWeightSystems,
        )

        languageDropdown.setAdapter(languageAdapter)
        currencyDropdown.setAdapter(currencyAdapter)
        weightSystemDropdown.setAdapter(weightSystemAdapter)

        languageDropdown.setText(Locale(preferredLanguage).displayLanguage, false)
        currencyDropdown.setText(preferredCurrency, false)
        weightSystemDropdown.setText(preferredWeightSystem, false)

        languageDropdown.setOnItemClickListener { _, _, index, _ ->
            val selectedLanguage: String = languageAdapter.getItem(index)!!
            val selectedLanguageCode: String = Locale.getAvailableLocales().find {
                it.displayLanguage.lowercase() == selectedLanguage.lowercase()
            }!!.language

            sharedPreferencesEditor.putString(
                Constants.SHARED_PREFERENCES_LANGUAGE_KEY,
                selectedLanguageCode,
            ).commit()

            Snackbar.make(
                requireView(),
                "Updated language to ${selectedLanguage.replaceFirstChar(Char::titlecase)}",
                Snackbar.LENGTH_SHORT,
            ).show()
        }
        currencyDropdown.setOnItemClickListener { _, _, index, _ ->
            val selectedCurrency: String = currencyAdapter.getItem(index)!!

            sharedPreferencesEditor.putString(
                Constants.SHARED_PREFERENCES_CURRENCY_KEY,
                selectedCurrency,
            ).commit()

            Snackbar.make(
                requireView(),
                "Updated currency to $selectedCurrency",
                Snackbar.LENGTH_SHORT,
            ).show()
        }
        weightSystemDropdown.setOnItemClickListener { _, _, index, _ ->
            val selectedWeightSystem: String = weightSystemAdapter.getItem(index)!!

            sharedPreferencesEditor.putString(
                Constants.SHARED_PREFERENCES_WEIGHT_SYSTEM_KEY,
                selectedWeightSystem,
            ).commit()

            Snackbar.make(
                requireView(),
                "Updated weight system to $selectedWeightSystem",
                Snackbar.LENGTH_SHORT,
            ).show()
        }

        val helpButton: MaterialButton = view.findViewById(R.id.help_button)
        helpButton.setOnClickListener {
            SettingsInfo().show(parentFragmentManager, "helpDialog")
        }
    }

}
