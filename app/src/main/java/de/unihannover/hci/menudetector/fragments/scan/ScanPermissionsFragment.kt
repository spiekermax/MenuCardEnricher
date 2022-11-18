package de.unihannover.hci.menudetector.fragments.scan

// Android
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

// Google Material
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

// Internal dependencies
import de.unihannover.hci.menudetector.R


private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class ScanPermissionsFragment : Fragment(R.layout.fragment_scan_permissions) {

    /* ATTRIBUTES */

    private val permissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val arePermissionsGranted: Boolean = permissions.entries.all {
            val isPermissionGranted: Boolean = it.value
            isPermissionGranted
        }

        when (arePermissionsGranted) {
            true -> onPermissionsGranted()
            false -> onPermissionsDenied()
        }
    }


    /* LIFECYCLE */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasPermissions()) {
            navigateToCamera()
        } else {
            requestPermissions()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: MaterialButton = view.findViewById(R.id.button)
        button.setOnClickListener { requestPermissionsManually() }
    }


    /* EVENT HANDLERS */

    private fun onPermissionsGranted() {
        Snackbar.make(requireView(), "Permissions granted", Snackbar.LENGTH_LONG)
            .setAction("Dismiss") {}
            .show()

        navigateToCamera()
    }

    private fun onPermissionsDenied() {
        Snackbar.make(requireView(), "Permissions denied", Snackbar.LENGTH_LONG)
            .setAction("Dismiss") {}
            .show()
    }


    /* METHODS */

    private fun navigateToCamera() {
        lifecycleScope.launchWhenStarted {
            findNavController().navigate(R.id.action_scanPermissionsFragment_to_scanFragment)
        }
    }

    private fun openSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireContext().packageName, null)
        )
        startActivity(intent)
    }

    private fun requestPermissions() {
        permissionRequestLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun requestPermissionsManually() {
        val isAnyPermissionPermanentlyDenied: Boolean = REQUIRED_PERMISSIONS.any {
            !shouldShowRequestPermissionRationale(it)
        }

        if (isAnyPermissionPermanentlyDenied) {
            openSettings()
        } else {
            requestPermissions()
        }
    }

    private fun hasPermissions(): Boolean {
        val context: Context = requireContext()
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

}