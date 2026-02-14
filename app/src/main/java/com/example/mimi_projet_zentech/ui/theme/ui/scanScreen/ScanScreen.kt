import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.R
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository
import com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen.ManualEntryDialog
import com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen.TopOptionsMenu
import com.example.mimi_projet_zentech.ui.theme.util.Screen
import com.yourapp.qrscanner.ui.components.ZxingQrScanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScannerScreen(navController: NavController, id: Int?) {
    // --- States ---
    var showManualDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    val repository = remember { HomeRepository() }
    val scope = rememberCoroutineScope()

    // --- 1. INFINITE ANIMATION LOGIC ---
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")

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
    val handleScanResult = { result: String ->
        val trimmed = result.trim()
        if (trimmed.isNotEmpty() && !isProcessing) {
            isProcessing = true
            scope.launch {
                delay(1200) // Beautiful Loader Duration

                // Ignore links, mark as NOT_FOUND to avoid crashes
                val isLink = trimmed.startsWith("http") || trimmed.startsWith("www")
                val scanStatus = if (isLink) ScanStatus.NOT_FOUND
                else repository.scanTicket(id, orderNumber = trimmed)

                // Uri.encode ensures slashes "/" don't break the navigation route
                val safeResult = Uri.encode(trimmed)

                navController.navigate(
                    Screen.ScanRes.getRoute(
                        buisnisId = id,
                        ticketNum = safeResult,
                        scanRes = scanStatus
                    )
                )
                isProcessing = false
            }
        }
    }

    // --- 3. UI LAYOUT ---
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // Camera Background
        ZxingQrScanner { result -> handleScanResult(result) }

        // Top Menu
        TopOptionsMenu(
            navController = navController,
            iconColor = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(16.dp)
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
                handleScanResult(passId)
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
        if (isProcessing) {
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
    }
}