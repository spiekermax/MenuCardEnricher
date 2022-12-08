package de.unihannover.hci.menudetector.fragments.scan

// Java
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Android
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

// Google
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.ListenableFuture

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.analyzer.MenuImageAnalyzer
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel


private const val CONFIDENCE_THRESHOLD = 0.8f

/**
 * Known bugs:
 * 1: No second analysis
 *     Steps to reproduce:
 *         1. Analyze an image with sufficient confidence
 *         2. Approve preview
 *         3. Go back
 *     Expected behaviour: A second scan will be accepted
 *     Actual behaviour: No images will be accepted
 */
class ScanCameraFragment : Fragment(R.layout.fragment_scan_camera) {

    /* ATTRIBUTES */

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraPreviewView: PreviewView

    private val cameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    private lateinit var navController: NavController
    private val viewModel by activityViewModels<MainActivityViewModel>()

    private var isRecognizedMenuApproved = false


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

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
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

    private fun onTakePictureClicked() {
        navController.navigate(R.id.action_scanCameraFragment_to_previewFragment)
    }

    private fun onMenuRecognized(menu: List<Dish>, confidence: Float?) {
        if (isRecognizedMenuApproved) return
        if (menu.isEmpty()) return

        if (confidence == null) return
        if (confidence < CONFIDENCE_THRESHOLD) return

        isRecognizedMenuApproved = true

        viewModel.preview = menu
        navController.navigate(R.id.action_scanCameraFragment_to_previewFragment)
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

        val takePictureButton: FloatingActionButton = view.findViewById(R.id.button_take_picture)
        takePictureButton.setOnClickListener { onTakePictureClicked() }
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

        val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder().build()
        imageAnalysis.setAnalyzer(cameraExecutor, MenuImageAnalyzer { menu, confidence ->
            onMenuRecognized(menu, confidence)
        })

        val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
    }

}