package com.yourapp.qrscanner.permission

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.util.Screen

@Composable

fun CameraPermission(
    businessId: Int?,
    onGranted: @Composable (() -> Unit),
     navController: NavController
    ) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted){
            navController.navigate(Screen.DeniedScreen.fullRoute(businessId)) {
                // Pop the scanner screen so they don't go back to a black screen
                popUpTo(Screen.ScannerScreen.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        onGranted()
    }
}

