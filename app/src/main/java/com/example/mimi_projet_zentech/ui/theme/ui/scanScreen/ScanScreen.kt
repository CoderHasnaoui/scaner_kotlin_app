import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.R

import com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen.ManualEntryDialog
import com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen.TopOptionsMenu
import com.example.mimi_projet_zentech.ui.theme.ui.scanScreen.ScanUiState
import com.example.mimi_projet_zentech.ui.theme.ui.scanScreen.ScanViewMode
import com.example.mimi_projet_zentech.ui.theme.util.Screen
import com.yourapp.qrscanner.ui.components.ZxingQrScanner
import kotlinx.coroutines.launch
import java.lang.Exception

@Composable
fun ScannerScreen(navController: NavController , viewModel: ScanViewMode = viewModel()) {
    // --- States ---
    val state  by viewModel.uiState.collectAsStateWithLifecycle()
    var showManualDialog by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) } // 🔹 Flash state


    // --- 1. INFINITE ANIMATION LOGIC ---
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")

    LaunchedEffect(Unit) {
        viewModel.checkToken()
        viewModel.onNavigate = {ticketNum  ,scanStatus ->
            navController.navigate(
                Screen.ScanRes.getRoute(ticketNum = ticketNum, scanRes = scanStatus)
            )
            viewModel.clearNavigation()
        }

    }
    // waith reponse for Api  if tocen Expizred
    if (state is ScanUiState.Initializing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        return
    }

    BackHandler {
        val previousRoute = navController.previousBackStackEntry?.destination?.route

        if (previousRoute == Screen.Home.route || previousRoute == null  || previousRoute == Screen.passwordConfirm.route) {
            //   previeus  is Home or none so  exit app
            (navController.context as? Activity)?.finish()
        } else {
            //  previous is Profile or Denied  back
            navController.popBackStack()
        }
    }
    // Pulse Animation (Scale)
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "framePulse"
    )

    // Laser Animation (Vertical Movement)
    val laserY by infiniteTransition.animateFloat(
        initialValue = -110f, // Top of the frame
        targetValue = 110f,   // Bottom of the frame
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "laserMove"
    )

    // --- 2. SCAN HANDLER ---





    // --- 3. UI LAYOUT ---
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)

    ) {

        // Camera Background
        ZxingQrScanner(
            isPaused = showManualDialog || state is ScanUiState.Verifiying,
            isFlashOn = isFlashOn,
            onLongPress = { isFlashOn = !isFlashOn },
            onResult = { result -> viewModel.handleScanResult(result) }
        )
        // Top Menu
        TopOptionsMenu(
            navController = navController,
            iconColor = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(16.dp) ,

        )

        // Animated Frame & Laser Container
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            // PULSING FRAME IMAGE
            Image(
                painter = painterResource(R.drawable.center_scan),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )

            // MOVING LASER LINE
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(2.dp)
                    .graphicsLayer { translationY = laserY.dp.toPx() }
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Red.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Manual Entry Dialog
        ManualEntryDialog(
            show = showManualDialog,
            onDismiss = { showManualDialog = false },
            onNext = { passId ->
                showManualDialog = false
                viewModel.handleScanResult(passId)
            }
        )

        // Check Manually Button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF1851C5)),
            onClick = { showManualDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = "Check Manually",
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // VERIFYING LOADER OVERLAY
        if (state is ScanUiState.Verifiying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 4.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Verifying Ticket...", color = Color.White)
                }
            }
        }
        Text(
            text = if (isFlashOn) "Flash is Active" else "Press long to turn flash on",
            color = Color.Yellow.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp)
        )
    }
}