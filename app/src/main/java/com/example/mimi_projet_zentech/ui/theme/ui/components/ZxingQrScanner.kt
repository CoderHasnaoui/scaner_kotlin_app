package com.yourapp.qrscanner.ui.components

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView

import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode


@Composable
fun ZxingQrScanner(
    isFlashOn: Boolean,
    isPaused: Boolean,
    onResult: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraController = remember { LifecycleCameraController(context) }

    LaunchedEffect(isPaused) {
        if (isPaused) {
            cameraController.unbind()
        } else {
            cameraController.bindToLifecycle(lifecycleOwner)
        }
    }

    LaunchedEffect(isFlashOn) {
        cameraController.enableTorch(isFlashOn)
    }

    LaunchedEffect(Unit) {
        cameraController.imageAnalysisTargetSize = CameraController.OutputSize(
            android.util.Size(1280, 720)
        )

        val barcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(context)
            ) { result ->
                if (!isPaused) {
                    val barcodeResults = result.getValue(barcodeScanner)
                    if (!barcodeResults.isNullOrEmpty()) {
                        barcodeResults[0].rawValue?.let { value ->
                            onResult(value)
                        }
                    }
                }
            }
        )

        cameraController.bindToLifecycle(lifecycleOwner)
        previewView.controller = cameraController
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}