package de.unihannover.hci.menudetector.fragments.scan

// Java
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Android
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

// Google
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.ListenableFuture

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.analyzers.MenuImageAnalyzer
import de.unihannover.hci.menudetector.models.image.ImageProperties
import de.unihannover.hci.menudetector.models.recognition.MenuRecognitionResult
import de.unihannover.hci.menudetector.views.GraphicOverlayView
import de.unihannover.hci.menudetector.views.graphics.MenuOverlayGraphic


class ScanCameraFragment : Fragment(R.layout.fragment_scan_camera) {

    /* ATTRIBUTES */

    private lateinit var cameraPreviewView: PreviewView
    private lateinit var graphicOverlayView: GraphicOverlayView

    private lateinit var backButton: FloatingActionButton
    private lateinit var takePictureButton: FloatingActionButton

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val cameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    private lateinit var navController: NavController

    private var menu: MenuRecognitionResult
        get() = menuChanges.value!!
        set(value) {
            menuChanges.value = value
        }
    private val menuChanges: MutableLiveData<MenuRecognitionResult> =
        MutableLiveData(MenuRecognitionResult())


    /* LIFECYCLE */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        bindViewListeners(view)

        bindListeners()

        startCameraPreview()
    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity?)?.supportActionBar?.hide()

        if (navController.previousBackStackEntry == null) {
            backButton.visibility = View.GONE
        } else {
            backButton.visibility = View.VISIBLE
        }

        menuChanges.value = MenuRecognitionResult()
    }

    override fun onStop() {
        super.onStop()

        (activity as AppCompatActivity?)?.supportActionBar?.show()

        menuChanges.value = MenuRecognitionResult()
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
        val action = ScanCameraFragmentDirections.actionScanCameraFragmentToSettingsFragment()
        navController.navigate(action)
    }

    private fun onMenuClicked() {
        val action = ScanCameraFragmentDirections.actionScanCameraFragmentToMenuFragment()
        navController.navigate(action)
    }

    private fun onOrderClicked() {
        val action = ScanCameraFragmentDirections.actionScanCameraFragmentToOrderFragment()
        navController.navigate(action)
    }

    private fun onTakePictureClicked() {
        val action = ScanCameraFragmentDirections.actionScanCameraFragmentToPreviewFragment(menu)
        navController.navigate(action)
    }

    private fun onInfoClicked() {
        val action = ScanCameraFragmentDirections.actionScanCameraFragmentToScanInfo()
        navController.navigate(action)
    }

    private fun onImagePropertiesChanged(imageProperties: ImageProperties?) {
        if (imageProperties == null) return

        graphicOverlayView.setImageSourceInfo(
            imageWidth = imageProperties.width,
            imageHeight = imageProperties.height,
            isFlipped = imageProperties.isMirrored,
        )
    }

    private fun onMenuRecognized(menu: MenuRecognitionResult) {
        this.menu = menu

        graphicOverlayView.clear()
        graphicOverlayView.add(
            MenuOverlayGraphic(
                overlay = graphicOverlayView,
                menu = menu,
            )
        )
    }


    /* METHODS */

    private fun bindViews(view: View) {
        cameraPreviewView = view.findViewById(R.id.camera_preview)
        graphicOverlayView = view.findViewById(R.id.graphic_overlay)

        backButton = view.findViewById(R.id.button_back)
        takePictureButton = view.findViewById(R.id.button_take_picture)
    }

    private fun bindViewListeners(view: View) {
        val backButton: FloatingActionButton = view.findViewById(R.id.button_back)
        backButton.setOnClickListener { onBackClicked() }

        val settingsButton: FloatingActionButton = view.findViewById(R.id.button_settings)
        settingsButton.setOnClickListener { onSettingsClicked() }

        val menuButton: FloatingActionButton = view.findViewById(R.id.button_menu)
        menuButton.setOnClickListener { onMenuClicked() }

        // val orderButton: FloatingActionButton = view.findViewById(R.id.button_order)
        // orderButton.setOnClickListener { onOrderClicked() }

        val infoButton: FloatingActionButton = view.findViewById(R.id.button_info)
        infoButton.setOnClickListener { onInfoClicked() }

        takePictureButton.setOnClickListener { onTakePictureClicked() }
    }

    private fun bindListeners() {
        menuChanges.observe(viewLifecycleOwner) {
            takePictureButton.isEnabled = !it.isEmpty()
        }
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
        imageAnalysis.setAnalyzer(cameraExecutor, MenuImageAnalyzer().apply {
            this.imagePropertiesChanges.observe(viewLifecycleOwner) {
                onImagePropertiesChanged(it)
            }

            this.menuChanges.observe(viewLifecycleOwner) {
                onMenuRecognized(it)
            }
        })

        val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
    }

}