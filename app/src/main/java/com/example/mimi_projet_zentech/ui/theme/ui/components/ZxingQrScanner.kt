package com.yourapp.qrscanner.ui.components

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView

import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures
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
    isPaused: Boolean, // This is what changes when Dialogue or Loading happens
    onResult: (String) -> Unit,
    onLongPress: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // --- THE FIX ---
    // This creates a "live" reference that the analyzer can read
    // even though the analyzer was created inside a LaunchedEffect(Unit)
    val pausedState by rememberUpdatedState(isPaused)

    val previewView = remember { PreviewView(context) }
    val cameraController = remember { LifecycleCameraController(context) }

    // Control the Flash
    LaunchedEffect(isFlashOn) {
        cameraController.enableTorch(isFlashOn)
    }

    // Setup Camera and Analyzer (Runs ONLY ONCE)
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
                // --- THE CRITICAL CHECK ---
                // We check the "live" pausedState here.
                // If it's true, we stop right here and ignore the scan.
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

    AndroidView(
        factory = { previewView },
        update = { view ->
            view.setOnLongClickListener {
                onLongPress()
                true
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}