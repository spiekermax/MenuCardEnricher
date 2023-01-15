package de.unihannover.hci.menudetector.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import de.unihannover.hci.menudetector.R

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scanMenuButton: MaterialButton = view.findViewById(R.id.button)
        scanMenuButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_scanPermissionsFragment)
        }
    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity?)?.supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()

        (activity as AppCompatActivity?)?.supportActionBar?.show()
    }

}