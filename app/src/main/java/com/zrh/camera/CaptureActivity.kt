package com.zrh.camera

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.zrh.camera.databinding.ActivityCaptureBinding
import java.io.File

/**
 *
 * @author zrh
 * @date 2023/7/20
 *
 */
class CaptureActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityCaptureBinding
    private lateinit var camera: Camera
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnCapture.setOnClickListener { capture() }

        mBinding.previewView.postDelayed(Runnable {
            startCamera()
        }, 500)
    }

    private fun startCamera() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(this)
        cameraProviderFeature.addListener(Runnable {
            val cameraProvider = cameraProviderFeature.get()

            val preview = Preview.Builder().build()

            imageCapture = ImageCapture.Builder()
                //                .setTargetResolution(getResolution())
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            camera =
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)


            preview.setSurfaceProvider(mBinding.previewView.surfaceProvider)

        }, ContextCompat.getMainExecutor(this))
    }

    private fun capture() {
        if (!this::imageCapture.isInitialized) return
        val cache = if (externalCacheDir != null) externalCacheDir else cacheDir
        val dir = File(cache, "capture")
        if (!dir.exists()) dir.mkdirs()
        val fileName = "${System.currentTimeMillis()}.jpg"
        val file = File(dir, fileName)
        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(options, ContextCompat.getMainExecutor(this), object :
            ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                toast("Success: ${file.absolutePath}")
                finish()
            }

            override fun onError(exception: ImageCaptureException) {
                toast("Error: $exception")
            }
        })
    }

    private fun getResolution(): Size {
        val displayMetrics = DisplayMetrics()
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display!!
        } else {
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay
        }
        display.getRealMetrics(displayMetrics)

        return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

}