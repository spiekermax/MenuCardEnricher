package de.unihannover.hci.menudetector.fragments

// Android
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("SHARED_PREF",Context.MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()
        val spLanguageValue = sharedPreferences.getInt("LANGUAGE",0)
        val spCurrencyValue = sharedPreferences.getInt("CURRENCY",0)
        val spWeightValue = sharedPreferences.getInt("WEIGHT",0)


        val spLanguage: Spinner = view.findViewById(R.id.spLanguage)
        val spCurrency: Spinner = view.findViewById(R.id.spCurrency)
        val spWeight: Spinner = view.findViewById(R.id.spWeight)



        spLanguage.setSelection(spLanguageValue)
        spCurrency.setSelection(spCurrencyValue)
        spWeight.setSelection(spWeightValue)

        spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing to do
            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                Toast.makeText(activity?.applicationContext,"You selected ${adapterView?.getItemAtPosition(position)
                    .toString()}",Toast.LENGTH_SHORT).show()

                editor.putInt("LANGUAGE",position).commit()
            }
        }

        spCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing to do
            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                Toast.makeText(activity?.applicationContext,"You selected ${adapterView?.getItemAtPosition(position)
                    .toString()}",Toast.LENGTH_SHORT).show()

                editor.putInt("CURRENCY",position).commit()
            }
        }

        spWeight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing to do
            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                Toast.makeText(activity?.applicationContext,"You selected ${adapterView?.getItemAtPosition(position)
                    .toString()}",Toast.LENGTH_SHORT).show()

                editor.putInt("WEIGHT",position).commit()
            }
        }

    }


}
