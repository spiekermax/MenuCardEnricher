package de.unihannover.hci.menudetector.fragments

// Android

// Internal dependencies
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.fragments.info.SettingsInfo
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import java.util.Currency
import java.util.*
import kotlin.collections.ArrayList


/**
 * TODO:
 * - Add options to dropdown (done)
 * - Select system language default (done)
 * - Remove start language dropdown (done)
 * - Store current value in SharedPreferences (done)
 * - Optional: Converter class between money and/or weight units
 */
class SettingsFragment : Fragment(R.layout.fragment_settings){
    private val viewModel by activityViewModels<MainActivityViewModel>()
    lateinit var sharedPreferences: SharedPreferences
    private var languagesLocalMap: Map<String,Locale> = HashMap<String,Locale>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController: NavController = findNavController()
        sharedPreferences = activity?.getSharedPreferences("SHARED_PREF",Context.MODE_PRIVATE)!!
        val editor = sharedPreferences!!.edit()
        //get Default system Language ans set it as default in app setting
        val defDeviceLang:String = Locale.getDefault().getDisplayLanguage()
        val locales: List<Locale> = Locale.getAvailableLocales().asList().distinctBy { it.language}
        val sortedlocals: List<Locale> = locales.sortedBy { it.getDisplayLanguage() }
        languagesLocalMap = sortedlocals.map { it.getDisplayName() to it }.toMap()
        val positionInLanguageArray = languagesLocalMap.keys.indexOf(defDeviceLang)
        //get currency
        val currencies: Set<Currency> = Currency.getAvailableCurrencies()

        //get the index of choosen dropdownlist item if exist, if not take the device default language
        val spLanguageValue = sharedPreferences.getInt("LANGUAGE", positionInLanguageArray)
        val spCurrencyValue = sharedPreferences.getInt("CURRENCY",0)
        val spWeightValue = sharedPreferences.getInt("WEIGHT",0)


        val spLanguage: Spinner = view.findViewById(R.id.spLanguage)
        val spCurrency: Spinner = view.findViewById(R.id.spCurrency)
        val spWeight: Spinner = view.findViewById(R.id.spWeight)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item, languagesLocalMap.keys.toList())

        spLanguage.adapter = adapter

        /*val adapter2: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item, languagesLocalMap.keys.toList())
        spCurrency.adapter = adapter2*/

        spLanguage.setSelection(spLanguageValue)
        spCurrency.setSelection(spCurrencyValue)
        spWeight.setSelection(spWeightValue)

        spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing to do
            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editor.putInt("LANGUAGE",position).commit()
            }
        }

        spCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing to do
            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editor.putInt("CURRENCY",position).commit()

            }
        }

        spWeight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing to do
            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editor.putInt("WEIGHT",position).commit()
            }
        }


        val infoButton: MaterialButton = view.findViewById(R.id.info_btn)
        infoButton.setOnClickListener {
            var dialog = SettingsInfo()
            dialog.show(getParentFragmentManager(), "infoDialog")
        }

    }


}
