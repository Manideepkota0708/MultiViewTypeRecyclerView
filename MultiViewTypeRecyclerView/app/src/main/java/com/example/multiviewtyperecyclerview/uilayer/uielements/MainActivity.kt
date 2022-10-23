package com.example.multiviewtyperecyclerview.uilayer.uielements

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.multiviewtyperecyclerview.R
import com.example.multiviewtyperecyclerview.uilayer.viewmodel.MainActivityViewModel
import com.example.multiviewtyperecyclerview.utils.LCE
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

private const val TAG = "MainActivity"


private val CAMERA_PERMISSIONS =
    listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray()


class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    lateinit var cameraProvider: ProcessCameraProvider
    lateinit var imageView: ImageView
    lateinit var id: String

    private val mainActivityViewModel by viewModels<MainActivityViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainActivityViewModel(application) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindFlows()
        mainActivityViewModel.loadAppData()
        findViewById<PreviewView>(R.id.previewView).setOnClickListener {
            onImageCaptured()
        }
    }


    private fun requestPermissionsAndStartCamera() {
        if (areAllPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, 10)
        }
    }

    private fun areAllPermissionsGranted() = CAMERA_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (areAllPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        Log.d(TAG, "cameraStarted")
        imageCapture = ImageCapture.Builder().build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(findViewById<PreviewView>(R.id.previewView).surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun onImageCaptured() {
        Log.d(TAG, "onImageCaptured")
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS",
            ConfigurationCompat.getLocales(resources.configuration)[0]
        )
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    mainActivityViewModel.imageMap[id] = output.savedUri!!
                    imageView.setImageURI(output.savedUri)
                    cameraProvider.unbindAll()
                    findViewById<PreviewView>(R.id.previewView).visibility = View.GONE
                }
            }
        )
    }

    private fun bindFlows() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.appDataStateFlow
                    .filterIsInstance<LCE.Content>()
                    .collect {
                        Log.d(TAG, it.appDataList.toString())
                        findViewById<RecyclerView>(R.id.recyclerView).apply {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            adapter = MultiViewTypeAdapter(
                                it.appDataList,
                                mainActivityViewModel.imageMap,
                                mainActivityViewModel.radioButtonMap,
                                mainActivityViewModel.switchMap,
                                mainActivityViewModel.commentMap

                            ) { imageView, id ->
                                this@MainActivity.imageView = imageView
                                this@MainActivity.id = id
                                this@MainActivity.findViewById<PreviewView>(R.id.previewView).visibility =
                                    View.VISIBLE
                                requestPermissionsAndStartCamera()
                            }
                        }
                    }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.appDataStateFlow
                    .filterIsInstance<LCE.Error>()
                    .collect {
                        Log.d(TAG, it.toString())
                        Toast.makeText(
                            this@MainActivity,
                            "please check input, unable to parse Json Array \n reason: ${it.errorMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }
}