package com.zrh.camera

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.zrh.camera.databinding.ActivityScanBinding

/**
 *
 * @author zrh
 * @date 2023/7/21
 *
 */
class QrcodeActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityScanBinding
    private lateinit var barcodeScanner: BarcodeScanner
    private var qrCodeDrawable: QrCodeDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityScanBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mBinding.root.postDelayed(Runnable { startCamera() }, 500)
    }

    private fun startCamera() {
        val cameraController = LifecycleCameraController(baseContext)
        val previewView = mBinding.previewView

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(this),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(this)
            ) { result: MlKitAnalyzer.Result? ->
                previewView.overlay.clear()

                val barcodeResults = result?.getValue(barcodeScanner)
                if ((barcodeResults == null) ||
                    (barcodeResults.size == 0) ||
                    (barcodeResults.first() == null)
                ) {
                    return@MlKitAnalyzer
                }

                val qrCodeViewModel = QrCodeViewModel(barcodeResults[0])
                if (qrCodeDrawable == null) {
                    qrCodeDrawable = QrCodeDrawable(qrCodeViewModel)
                } else {
                    qrCodeDrawable!!.setModel(qrCodeViewModel)
                }
                previewView.overlay.add(qrCodeDrawable!!)
            }
        )

        cameraController.bindToLifecycle(this)
        previewView.controller = cameraController
    }
}