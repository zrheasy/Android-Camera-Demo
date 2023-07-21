package com.zrh.camera

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import com.zrh.camera.databinding.ActivityRecordBinding
import java.io.File

/**
 *
 * @author zrh
 * @date 2023/7/20
 *
 */
class VideoRecordActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityRecordBinding
    private var recording = false
    private var mRecorder: Recording? = null
    private var videoCapture: VideoCapture<Recorder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnRecord.setOnClickListener {
            if (recording) {
                stopRecord()
            } else {
                startRecord()
            }
        }

        mBinding.previewView.postDelayed(Runnable {
            startCamera()
        }, 500)
    }

    private fun onDurationChanged(duration: Long) {
        val seconds = duration % 60
        val minutes = duration / 60
        val time = "${formatNum(minutes)}:${formatNum(seconds)}"
        mBinding.tvDuration.text = time
    }

    private fun formatNum(num: Long): String {
        return if (num < 10) "0$num" else num.toString()
    }

    @SuppressLint("MissingPermission")
    private fun startRecord() {
        if (videoCapture == null) return
        recording = true
        val cache = if (externalCacheDir != null) externalCacheDir else cacheDir
        val dir = File(cache, "video-record")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "${System.currentTimeMillis()}.mp4")
        val options = FileOutputOptions.Builder(file).build()
        mRecorder = videoCapture!!.output
            .prepareRecording(this, options)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(this), Consumer<VideoRecordEvent> {
                if (it is VideoRecordEvent.Status) {
                    onDurationChanged(it.recordingStats.recordedDurationNanos/1000_000_000)
                } else if (it is VideoRecordEvent.Finalize) {
                    if (!it.hasError()) {
                        onComplete(it.outputResults.outputUri)
                    }
                }
            })
    }

    private fun onComplete(outputUri: Uri) {
        toast(outputUri.toString())
        finish()
    }

    private fun stopRecord() {
        recording = false
        mRecorder?.stop()
        mRecorder?.close()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val provider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()

            val qualitySelector = QualitySelector.fromOrderedList(listOf(Quality.FHD, Quality.HD))
            val recorder = Recorder.Builder()
                .setQualitySelector(qualitySelector)
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            provider.bindToLifecycle(this, cameraSelector, preview, videoCapture)

            preview.setSurfaceProvider(mBinding.previewView.surfaceProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        mRecorder?.close()
    }

}