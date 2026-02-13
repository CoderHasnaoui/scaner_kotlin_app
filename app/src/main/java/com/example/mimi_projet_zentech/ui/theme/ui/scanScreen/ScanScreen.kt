import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat

import com.example.mimi_projet_zentech.R


import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository
import com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen.TopOptionsMenu
import com.example.mimi_projet_zentech.ui.theme.util.Screen
import com.yourapp.qrscanner.ui.components.ZxingQrScanner
//import com.yourapp.qrscanner.ui.components.startQuickScan


@Composable
fun ScannerScreen(navController: NavController, id: Int?) {
    // 1. Manage the Status Bar Icons
//    DisposableEffect(Unit) {
//        insetsController.isAppearanceLightStatusBars = false
//        onDispose { insetsController.isAppearanceLightStatusBars = true }
//    }
    val repository = HomeRepository()
    var contex  = LocalContext.current
    var scanStatus : ScanStatus
    // Use a Box to layer everything
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // 2️⃣ THE CAMERA (ZXing) - This is the background
        ZxingQrScanner { result ->
            // Handle your QR result here!

             scanStatus = repository.scanTicket(id , orderNumber = result)
            Toast.makeText(contex , "id == $id  scan Status = $scanStatus" , Toast.LENGTH_LONG ).show()
              navController.navigate(Screen.ScanRes.getRoute(
                    buisnisId = id,
                    ticketNum = result,
                    scanRes = scanStatus
                ))
        }

        TopOptionsMenu(
            navController = navController,
            iconColor = Color.White, // White looks better on camera
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(16.dp)
        )

//        IconButton(
//            onClick = { /* TODO */ },
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(WindowInsets.statusBars.asPaddingValues())
//                .padding(16.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.MoreVert,
//                contentDescription = null,
//                tint = Color.White
//            )
//        }

        // 4️⃣ CENTER SCAN FRAME (The visual guide for the user)
        Image(
            painter = painterResource(R.drawable.center_scan),
            contentDescription = null,
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.Center)
        )


        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(16.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D58D1))
        ) {
            Text(
                text = "Check Manually",
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}


@Composable

fun ScanPageScreen(insetsController: WindowInsetsControllerCompat) {

    DisposableEffect(Unit) {
        // 1. Make icons WHITE for this screen (because of the background image)
        insetsController.isAppearanceLightStatusBars = false

        onDispose {
            // 2. IMPORTANT: When leaving this screen, reset icons to DARK
            // so the Sign-In screen is readable again!
            insetsController.isAppearanceLightStatusBars = true
        }
    }
    // We use Box to stack the UI elements
    Box(modifier = Modifier.fillMaxSize()) {

        // 1️⃣ Background Image (Must be first to be at the bottom)
        Image(
            painter = painterResource(R.drawable.center_scan), // it was scan screen
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )


        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(WindowInsets.statusBars.asPaddingValues()) // Protects from top bar
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color.White
            )
        }

        // 3️⃣ Center Scan Frame
        Image(
            painter = painterResource(R.drawable.center_scan),
            contentDescription = null,
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.Center)
        )

        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues()) // Protects from bottom bar
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(16.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D58D1))
        ) {
            Text(
                text = "Check Manually",
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}






