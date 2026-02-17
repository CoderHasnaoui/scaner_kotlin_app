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
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository
import com.example.mimi_projet_zentech.ui.theme.util.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import android.provider.Settings
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
fun DeniedScreen(navController: NavController, businessId: Int?) {
    val repository = remember { HomeRepository() }
    val scope = rememberCoroutineScope()
    var showManualDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LifecycleResumeEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // User just came back from settings and gave permission!
            // Send them straight to the scanner.
            navController.navigate(Screen.ScannerScreen.fullRoute(businessId)) {
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

        /* 🔹 TOP RIGHT MENU (ICON + DROPDOWN) */

Box( modifier = Modifier
    .align(Alignment.TopEnd)
    .padding(WindowInsets.statusBars.asPaddingValues())
    .padding(16.dp)){
    TopOptionsMenu(navController , buisnesId =businessId)
}
        /* 🔹 CENTER IMAGE */
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

                // --- START OF THE "TACHE" (THE LOGIC) ---
                val trimmed = passId.trim()
                if (trimmed.isNotEmpty() && !isProcessing) {
                    isProcessing = true
                    scope.launch {
                        delay(1200) // Beautiful Loader Duration

                        // Call the repository exactly like ScannerScreen does
                        val scanStatus = repository.scanTicket(businessId, passId.trim())

                        val safeResult = android.net.Uri.encode(trimmed)

                        // Navigate to the same Result Screen
                        navController.navigate(
                            Screen.ScanRes.getRoute(
                                buisnisId = businessId,
                                ticketNum = safeResult,
                                scanRes = scanStatus
                            )
                        )
                        isProcessing = false
                    }

                }
            }
        )

        /* 🔹 BOTTOM BUTTONS */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = {  showManualDialog =true},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1851C5)
                )
            ) {
                Text(
                    text = "Check Manually",
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    // 1. Check current status
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        // 2. If they ALREADY accepted, go to Scanner
                        navController.navigate(Screen.ScannerScreen.fullRoute(businessId)) {
                            popUpTo(Screen.DeniedScreen.route) { inclusive = true }
                        }
                    } else {
                        // 3. If they still haven't accepted, go to Settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor =MaterialTheme.colorScheme.background
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

    if (isProcessing) {
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


@Composable
fun TopOptionsMenu(
    navController: NavController,
    modifier: Modifier = Modifier,
    iconColor: Color = Color.Black // Added this so you can change it for the camera screen
    , buisnesId: Int?
) {
    var showMenu by remember { mutableStateOf(false) }

    // 1. This Box is the "Anchor". The menu will stay attached to this Box.
    Box(modifier = modifier) {
        IconButton(onClick = { showMenu = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.onBackground
        ) {
            DropdownMenuItem(
                text = { Text("Profile", color = MaterialTheme.colorScheme.background) },
                onClick = {
                    showMenu = false
                    val idToSend = buisnesId ?: -1
                    navController.navigate(Screen.Profile.fullRoute(idToSend))
                }
            )
        }
    }
}





@Composable
fun ManualEntryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onNext: (String) -> Unit
) {
    if (!show) return

    // Use rememberSaveable so text doesn't disappear on rotate
    var passId by rememberSaveable { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss ,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            // UsePlatformDefaultWidth = false allows the card to be wider if needed
            usePlatformDefaultWidth = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Add some outer padding
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onBackground,
                        text = "Trouble scanning?Enter manually", // Added newline for better fit
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f) // Give text room
                    )

                    IconButton(onClick = {
                        passId = "" // Clear text on close
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Input
                OutlinedTextField(
                    value = passId,
                    onValueChange = { passId = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter Pass ID ") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Button
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0XFF1851C5),
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f) // Style for disabled
                    ),
                    onClick = {
                        if (passId.isNotBlank()) {
                            onNext(passId)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp),
                    // 🔹 Only enable if user typed something
                    enabled = passId.isNotBlank()
                ) {
                    Text(
                        "Next",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
