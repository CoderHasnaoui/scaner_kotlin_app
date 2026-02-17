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
    isFlashOn: Boolean,
    isPaused: Boolean,
    onResult: (String) -> Unit
) {
    AndroidView(
        factory = { context ->
            DecoratedBarcodeView(context).apply {
                // 🔹 FIX: Clear the default status text
                setStatusText("")
                barcodeView.decoderFactory =
                    DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))

                decodeContinuous { result ->
                    result.text?.let {
                        onResult(it)
                        pause() // 🔒 stop scanning after first result
                    }
                }

            }
        },
        update = { view ->
            // 🔹 This runs whenever isPaused changes

            // Handle Pause/Resume
            if (isPaused) view.pause() else view.resume()

            // 🔹 Handle Flashlight
            if (isFlashOn) {
                view.setTorchOn()
            } else {
                view.setTorchOff()
            }

        }
    )
}
