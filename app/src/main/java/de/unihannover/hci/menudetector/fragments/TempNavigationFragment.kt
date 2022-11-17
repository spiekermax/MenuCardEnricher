package de.unihannover.hci.menudetector.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import de.unihannover.hci.menudetector.R

class TempNavigationFragment : Fragment(R.layout.fragment_temp_navigation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dishButton: MaterialButton = view.findViewById(R.id.button_dish)
        val menuButton: MaterialButton = view.findViewById(R.id.button_menu)
        val orderButton: MaterialButton = view.findViewById(R.id.button_order)
        val previewButton: MaterialButton = view.findViewById(R.id.button_preview)
        val scanButton: MaterialButton = view.findViewById(R.id.button_scan)
        val settingsButton: MaterialButton = view.findViewById(R.id.button_settings)

        dishButton.setOnClickListener {
            findNavController().navigate(R.id.action_tempNavigationFragment_to_dishFragment)
        }

        menuButton.setOnClickListener {
            findNavController().navigate(R.id.action_tempNavigationFragment_to_menuFragment)
        }

        orderButton.setOnClickListener {
            findNavController().navigate(R.id.action_tempNavigationFragment_to_orderFragment)
        }

        previewButton.setOnClickListener {
            findNavController().navigate(R.id.action_tempNavigationFragment_to_previewFragment)
        }

        scanButton.setOnClickListener {
            findNavController().navigate(R.id.action_tempNavigationFragment_to_scanFragment)
        }

        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_tempNavigationFragment_to_settingsFragment)
        }
    }

}
