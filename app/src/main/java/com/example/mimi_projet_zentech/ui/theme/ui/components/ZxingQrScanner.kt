package com.yourapp.qrscanner.ui.components

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView

import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
    onResult: (String) -> Unit,
    onLongPress: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val pausedState by rememberUpdatedState(isPaused)

    val previewView = remember { PreviewView(context) }
    val cameraController = remember { LifecycleCameraController(context) }

    // Control the Flash
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

                if (!pausedState) {
                    val barcodeResults = result.getValue(barcodeScanner)
                    if (!barcodeResults.isNullOrEmpty()) {
                        barcodeResults[0].rawValue?.let { value ->
                            // Check one more time before calling onResult
                            if (!pausedState) {
                                onResult(value)
                            }
                        }
                    }
                }
            }
        )

        cameraController.bindToLifecycle(lifecycleOwner)
        previewView.controller = cameraController
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera
        AndroidView(
            factory = { previewView },
            update = { },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onLongPress() }
                    )
                }
        )
    }
}