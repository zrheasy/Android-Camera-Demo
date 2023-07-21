package com.zrh.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.zrh.camera.databinding.ActivityMainBinding
import com.zrh.permission.PermissionUtils

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnCapture.setOnClickListener { requestCapture() }
        mBinding.btnRecord.setOnClickListener { requestVideoRecord() }
        mBinding.btnQrcode.setOnClickListener { requestQrcode() }
    }

    private fun requestQrcode() {
        PermissionUtils.requestPermissions(this, arrayOf(Manifest.permission.CAMERA)) { _, granted ->
            if (granted) {
                startActivity(QrcodeActivity::class.java)
            }
        }
    }

    private fun requestCapture() {
        PermissionUtils.requestPermissions(this, arrayOf(Manifest.permission.CAMERA)) { _, granted ->
            if (granted) {
                startActivity(CaptureActivity::class.java)
            }
        }
    }

    private fun requestVideoRecord() {
        PermissionUtils.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) { _, granted ->
            if (granted) {
                startActivity(VideoRecordActivity::class.java)
            }
        }
    }

    private fun startActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}