package de.unihannover.hci.menudetector.fragments.scan

// Android

// Google

// Internal dependencies
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import de.unihannover.hci.menudetector.R


class ScanCameraFragment : Fragment(R.layout.fragment_scan_camera) {

    /* ATTRIBUTES */

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraPreviewView: PreviewView

    private lateinit var navController: NavController


    /* LIFECYCLE */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        bindViewListeners(view)

        startCameraPreview()
    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity?)?.supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()

        (activity as AppCompatActivity?)?.supportActionBar?.show()
    }


    /* EVENT HANDLERS */

    private fun onBackClicked() {
        navController.popBackStack()
    }

    private fun onSettingsClicked() {
        navController.navigate(R.id.action_scanCameraFragment_to_settingsFragment)
    }

    private fun onMenuClicked() {
        navController.navigate(R.id.action_scanCameraFragment_to_menuFragment)
    }

    private fun onOrderClicked() {
        navController.navigate(R.id.action_scanCameraFragment_to_orderFragment)
    }


    /* METHODS */

    private fun bindViews(view: View) {
        cameraPreviewView = view.findViewById(R.id.camera_preview)
    }

    private fun bindViewListeners(view: View) {
        val backButton: FloatingActionButton = view.findViewById(R.id.button_back)
        backButton.setOnClickListener { onBackClicked() }

        val settingsButton: FloatingActionButton = view.findViewById(R.id.button_settings)
        settingsButton.setOnClickListener { onSettingsClicked() }

        val menuButton: FloatingActionButton = view.findViewById(R.id.button_menu)
        menuButton.setOnClickListener { onMenuClicked() }

        val orderButton: FloatingActionButton = view.findViewById(R.id.button_order)
        orderButton.setOnClickListener { onOrderClicked() }
    }

    private fun startCameraPreview() {
        val context: Context = requireContext()

        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCameraPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        preview.setSurfaceProvider(cameraPreviewView.surfaceProvider)

        val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview)
    }

}