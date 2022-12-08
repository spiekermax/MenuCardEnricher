package de.unihannover.hci.menudetector.fragments

// Android
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

// Google Material
import com.google.android.material.button.MaterialButton

// Internal dependencies
import de.unihannover.hci.menudetector.R


class TempNavigationFragment : Fragment(R.layout.fragment_temp_navigation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController: NavController = findNavController()


        val menuButton: MaterialButton = view.findViewById(R.id.button_menu)
        menuButton.setOnClickListener {
            navController.navigate(R.id.action_tempNavigationFragment_to_menuFragment)
        }

        val orderButton: MaterialButton = view.findViewById(R.id.button_order)
        orderButton.setOnClickListener {
            navController.navigate(R.id.action_tempNavigationFragment_to_orderFragment)
        }

        val previewButton: MaterialButton = view.findViewById(R.id.button_preview)
        previewButton.setOnClickListener {
            navController.navigate(R.id.action_tempNavigationFragment_to_previewFragment)
        }

        val scanButton: MaterialButton = view.findViewById(R.id.button_scan)
        scanButton.setOnClickListener {
            navController.navigate(R.id.action_tempNavigationFragment_to_scanPermissionsFragment)
        }

        val settingsButton: MaterialButton = view.findViewById(R.id.button_settings)
        settingsButton.setOnClickListener {
            navController.navigate(R.id.action_tempNavigationFragment_to_settingsFragment)
        }
    }

}