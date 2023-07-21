package com.zrh.camera

import android.graphics.Rect
import com.google.mlkit.vision.barcode.common.Barcode

class QrCodeViewModel(barcode: Barcode) {
    var boundingRect: Rect = barcode.boundingBox!!
    var qrContent: String = barcode.rawValue ?: "Unsupported data type: ${barcode.rawValue.toString()}"
}