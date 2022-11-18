package de.unihannover.hci.menudetector.fragments.scan

// Android
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

// Google
import com.google.common.util.concurrent.ListenableFuture

// Internal dependencies
import de.unihannover.hci.menudetector.R


class ScanCameraFragment : Fragment(R.layout.fragment_scan_camera) {

    /* ATTRIBUTES */

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraPreviewView: PreviewView


    /* LIFECYCLE */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        startCameraPreview()
    }


    /* METHODS */

    private fun bindViews(view: View) {
        cameraPreviewView = view.findViewById(R.id.camera_preview)
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
        cameraProvider.bindToLifecycle(this, cameraSelector, preview)
    }

}