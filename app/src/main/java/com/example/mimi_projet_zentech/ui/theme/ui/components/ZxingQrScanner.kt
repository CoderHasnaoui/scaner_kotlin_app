package com.yourapp.qrscanner.ui.components

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory


@Composable
fun ZxingQrScanner(
    onResult: (String) -> Unit
) {
    AndroidView(
        factory = { context ->
            DecoratedBarcodeView(context).apply {
                barcodeView.decoderFactory =
                    DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))

                decodeContinuous { result ->
                    result.text?.let {
                        onResult(it)
                        pause() // 🔒 stop scanning after first result
                    }
                }

                resume()
            }
        }
    )
}
