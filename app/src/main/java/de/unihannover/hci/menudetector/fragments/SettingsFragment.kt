package de.unihannover.hci.menudetector.fragments

// Android
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment

// Internal dependencies
import de.unihannover.hci.menudetector.R


/**
 * TODO:
 * - Add options to dropdown
 * - Select system language default
 * - Remove start language dropdown (done)
 * - Store current value in SharedPreferences
 * - Optional: Converter class between money and/or weight units
 */
class SettingsFragment : Fragment(R.layout.fragment_settings)
