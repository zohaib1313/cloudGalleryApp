package ir.shahabazimi.instagrampicker.gallery

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yalantis.ucrop.UCrop
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.R
import ir.shahabazimi.instagrampicker.classes.Statics
import ir.shahabazimi.instagrampicker.databinding.FragmentCameraBinding
import ir.shahabazimi.instagrampicker.filter.FilterActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

enum class FlashMode {
    FLASH_ON,
    FLASH_OFF,
    FLASH_AUTO
}

class CameraFragment : Fragment(R.layout.fragment_camera) {

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private lateinit var b: FragmentCameraBinding
    private var isFront = false
    private var imageCapture: ImageCapture? = null
    private var flash = FlashMode.FLASH_OFF
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentCameraBinding.inflate(inflater, container, false)
        return b.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = getString(R.string.instagrampicker_camera_title)

        startCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        b.cCapture.setOnClickListener { takePhoto() }

        b.cChange.setOnClickListener {
            isFront = if (isFront) {
                startCamera(CameraSelector.DEFAULT_BACK_CAMERA)
                false
            } else {
                startCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
                true
            }
        }

        b.cFlash.setOnClickListener {
            when (flash) {
                FlashMode.FLASH_OFF -> {
                    flash = FlashMode.FLASH_AUTO
                    b.cFlash.setImageResource(R.mipmap.ic_flash_auto)
                }
                FlashMode.FLASH_AUTO -> {
                    flash = FlashMode.FLASH_ON
                    b.cFlash.setImageResource(R.mipmap.ic_flash_on)
                }
                FlashMode.FLASH_ON -> {
                    flash = FlashMode.FLASH_OFF
                    b.cFlash.setImageResource(R.mipmap.ic_flash_off)
                }
            }

        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(b.cViewFinder.surfaceProvider)
                    }
            imageCapture = Builder()
                    .build()
            try {
                cameraProvider.unbindAll()

                val camera = cameraProvider.bindToLifecycle(
                        requireActivity(), cameraSelector, preview, imageCapture)

                b.cViewFinder.afterMeasured {
                    b.cViewFinder.setOnTouchListener { _, event ->
                        return@setOnTouchListener when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                b.cFocus.visibility = View.VISIBLE
                                val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                                        b.cViewFinder.width.toFloat(), b.cViewFinder.height.toFloat()
                                )
                                val autoFocusPoint = factory.createPoint(event.x, event.y)
                                try {

                                    camera.cameraControl.startFocusAndMetering(
                                            FocusMeteringAction.Builder(
                                                    autoFocusPoint,
                                                    FocusMeteringAction.FLAG_AF
                                            ).apply {
                                                disableAutoCancel()
                                            }.build()
                                    )
                                    b.cFocus.translationX = event.x - (b.cFocus.width / 2)
                                    b.cFocus.translationY = event.y - (b.cFocus.height / 2)
                                } catch (e: CameraInfoUnavailableException) {
                                }
                                true
                            }
                            else -> false
                        }
                    }
                }
            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private inline fun View.afterMeasured(crossinline block: () -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File.createTempFile(
                SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()),
                ".jpg",
                outputDirectory
        )

        val outputOptions = OutputFileOptions.Builder(photoFile).build()

        when (flash) {
            FlashMode.FLASH_ON -> imageCapture.flashMode = FLASH_MODE_ON
            FlashMode.FLASH_OFF -> imageCapture.flashMode = FLASH_MODE_OFF
            FlashMode.FLASH_AUTO -> imageCapture.flashMode = FLASH_MODE_AUTO
        }

        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(context), object : OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
            }

            override fun onImageSaved(output: OutputFileResults) {
                startCropping(photoFile)
            }
        })
    }

    private fun startCropping(f: File) {
        val x = InstagramPicker.x
        val y = InstagramPicker.y
        val options = UCrop.Options()
        options.setToolbarTitle(getString(R.string.instagrampicker_crop_title))
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.withMaxResultSize(2000,2000)
        UCrop.of(Uri.fromFile(f), Uri.fromFile(File(requireActivity().cacheDir, Statics.getCurrentDate())))
                .withAspectRatio(x, y)
                .withOptions(options)
                .start(requireContext(), this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && data!=null) {
            val resultUri = UCrop.getOutput(data)
            val `in` = Intent(requireContext(), FilterActivity::class.java)
            `in`.putExtra("uri", resultUri)
            FilterActivity.picAddress = resultUri
            startActivityForResult(`in`, 444)
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}