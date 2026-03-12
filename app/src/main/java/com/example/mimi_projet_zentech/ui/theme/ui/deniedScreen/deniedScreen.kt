    package com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.net.Uri
    import androidx.compose.ui.window.DialogProperties
    import androidx.compose.foundation.BorderStroke
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Close
    import androidx.compose.material.icons.filled.MoreVert
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.runtime.saveable.rememberSaveable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.window.Dialog
    import androidx.navigation.NavController
    import com.example.mimi_projet_zentech.R
    import com.example.mimi_projet_zentech.ui.theme.util.Screen
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch

    import android.provider.Settings
    import androidx.core.content.ContextCompat
    import android.Manifest
    import androidx.lifecycle.compose.LifecycleResumeEffect
    import androidx.lifecycle.compose.collectAsStateWithLifecycle
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.example.mimi_projet_zentech.ui.theme.ui.scanScreen.ScanUiState
    import com.example.mimi_projet_zentech.ui.theme.ui.scanScreen.ScanViewMode


    @Composable
    fun DeniedScreen(navController: NavController , viewModel: ScanViewMode) {
        val uistate= viewModel.uiState.collectAsStateWithLifecycle()
        var showManualDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current


        LaunchedEffect(Unit) {
            viewModel.onNavigate = { ticketNum, scanStatus ->
                navController.navigate(
                    Screen.ScanRes.getRoute(
                        ticketNum = ticketNum,
                        scanRes = scanStatus
                    )
                )
            }
        }

        LifecycleResumeEffect(Unit) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                navController.navigate(Screen.ScannerScreen.route) {
                    popUpTo(Screen.DeniedScreen.route) { inclusive = true }
                }
            }
            onPauseOrDispose { }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(16.dp)
            ) {
                TopOptionsMenu(navController)
            }

            Image(
                painter = painterResource(R.drawable.denied_bg),
                contentDescription = null,
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.Center)
            )

            ManualEntryDialog(
                show = showManualDialog,
                onDismiss = { showManualDialog = false },
                onNext = { passId ->
                    showManualDialog = false
                    viewModel.handleScanResult(passId)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showManualDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1851C5))
                ) {
                    Text(
                        text = "Check Manually",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            navController.navigate(Screen.ScannerScreen.route) {
                                popUpTo(Screen.DeniedScreen.route) { inclusive = true }
                            }
                        } else {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    border = BorderStroke(1.dp, Color(0xFF1D58D1))
                ) {
                    Text(
                        text = "Camera Permission",
                        color = Color(0xFF1D58D1),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        // 👇 loading overlay
        if (uistate is ScanUiState.Verifiying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Verifying Pass ID...", color = Color.White)
                }
            }
        }
    }






