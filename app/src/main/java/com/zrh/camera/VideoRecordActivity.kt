package com.zrh.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zrh.camera.databinding.ActivityCaptureBinding

/**
 *
 * @author zrh
 * @date 2023/7/20
 *
 */
class VideoRecordActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityCaptureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

}