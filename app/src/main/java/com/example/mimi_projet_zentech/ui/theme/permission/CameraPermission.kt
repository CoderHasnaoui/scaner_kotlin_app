package com.yourapp.qrscanner.permission

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.util.Screen

@Composable

fun CameraPermission(

    onGranted: @Composable (() -> Unit),
     navController: NavController
    ) {
    val context = LocalContext.current
    val token = TokenManager(context) ;
    val slug :String?  = token.getSlug()
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
            navController.navigate(Screen.DeniedScreen.route) {
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

