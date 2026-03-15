package com.example.mimi_projet_zentech.ui.theme.ui.profileScreen
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.R // Your package name here
import com.example.mimi_projet_zentech.ui.theme.util.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Business

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextDecoration
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInInput
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignIncon
import javax.crypto.Cipher


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileScreen(
    navController: NavController,
    isDarkModeState: MutableState<Boolean>, // This controls the app theme
    viewModel: ProfileViewModel = viewModel()
) {
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    // Trigger API load once
    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }
    if (!viewModel.isReady) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. User Info Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(60.dp).background(Color(0XFFF4F4F5), CircleShape), contentAlignment = Alignment.Center) {
                        Text(viewModel.userInitial ,  fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121212))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(userEmail, color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Your selected group", modifier = Modifier.fillMaxWidth(), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))

            // --- 2. Business Group Card (MVVM API Data) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0x68FAFAFB))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                            Icon(Icons.Default.Business, null, modifier = Modifier.padding(10.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            // loading State
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            }
                            // Error state
                            else if ((viewModel.errorMessage != null)){
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                                    TextButton(onClick = { viewModel.loadProfileData() }) {
                                        Text("Retry", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            // Succes State
                            else {
                                val merchant = viewModel.selectedMerchant
                                Column {
                                    Text(merchant?.name ?: "No Selection", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    val tag = merchant?.name?.let { "@${it.replace(" ", "").lowercase()}" } ?: "@unknown"
                                    Text(tag, fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }

                        Button(

                            onClick = { viewModel.slugManager.clearSelectedSlug()
                                navController.navigate(Screen.Home.getRoute(forceSelect = true)) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                } },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1851C5)),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.height(32.dp),
                            // 2. Reduce internal padding so the text fits
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        ) {
                            Text("Change", color = Color.White)
                        }
                    }
                    // Location List
                    if (viewModel.selectedMerchant != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                viewModel.selectedMerchant?.locations?.forEach { loc ->
                                    Text(loc.name, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. Dark Mode (FIXED HERE) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Switch(
                    checked = isDarkModeState.value, // Uses the state passed from MainActivity
                    onCheckedChange = { newValue ->
                        isDarkModeState.value = newValue // Updates the global theme
//                        sharedPref.edit().putBoolean("is_dark_mode", newValue).apply() // Persists it
                        viewModel.themeRepo.setDarkMode(newValue)
                    }
                )
            }

            // --- 4. Biometric ---

            BiometricSwitchSection(
                viewModel = viewModel,
                email = userEmail  // ← pass current user email
            )


            Spacer(modifier = Modifier.weight(1f))

            // --- 4. Log Out ---
            Button(
                onClick = {
                    viewModel.tokenManager.clearToken()
                    viewModel.tokenManager.logOut()
                    viewModel.slugManager.clearSelectedSlug()
                    navController.navigate(Screen.Account.route) { popUpTo(0) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDE0000)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun BiometricSwitchSection(
    viewModel: ProfileViewModel,
    email: String
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()
    val isSwitchOn = isBiometricEnabled && currentUser?.encryptedPassword != null


    var showPasswordDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // ← BiometricPrompt lives here in the screen
    fun launchEncryptPrompt(cipher: Cipher, plainPassword: String) {
        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    val authenticatedCipher = result.cryptoObject?.cipher!!
                    // ← send authenticated cipher back to ViewModel
                    viewModel.saveEncryptedPassword(
                        authenticatedCipher,
                        plainPassword,
                        email
                    )
                    showPasswordDialog = false
                    password = ""
                    isLoading = false
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    passwordError = "Fingerprint cancelled"
                    isLoading = false
                }

                override fun onAuthenticationFailed() {}
            }
        )

        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Enable Fingerprint Login")
                .setSubtitle("Verify your fingerprint to enable quick login")
                .setNegativeButtonText("Cancel")
                .build(),
            BiometricPrompt.CryptoObject(cipher)
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasswordDialog = false
                password = ""
                passwordError = null
            },
            title = { Text("Enable Fingerprint Login") },
            text = {
                Column {
                    Text(
                        "Enter your password to enable fingerprint login",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(12.dp))
                    SignInInput(
                        title = "password",
                        value = password,

                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        icon = SignIncon.Password,
                        isPasswordField = true,
                        errorMessage = passwordError,
                        enableField = !isLoading
                    )
                    if (isLoading) {
                        Spacer(Modifier.height(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterHorizontally),
                            strokeWidth = 2.dp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (password.isEmpty()) {
                            passwordError = "Field is required"
                            return@TextButton
                        }
                        if (password.length < 8) {
                            passwordError = "Password must be at least 8 characters"
                            return@TextButton
                        }
                        isLoading = true
                        viewModel.verifyPasswordAndGetCipher(
                            password = password,
                            email = email,
                            onSuccess = { cipher, plainPassword ->
                                // ← launch biometric prompt from screen
                                launchEncryptPrompt(cipher, plainPassword)
                            },
                            onError = { error ->
                                isLoading = false
                                passwordError = error
                            }
                        )
                    },
                    enabled = !isLoading
                ) {
                    Text("Confirm", color = Color(0xFF0064e0))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasswordDialog = false
                    password = ""
                    passwordError = null
                }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Fingerprint Login",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = isSwitchOn,
            onCheckedChange = { newValue ->
                if (newValue) {
                    viewModel.onBiometricSwitchEnabled(
                        onNeedPassword = {
                            showPasswordDialog = true  // ← no encrypted password → ask password
                        },
                        onDirectEnable = {
                            // ← already has encrypted password → just enable
                            // nothing to show, already handled in ViewModel
                        }
                    )                } else {
                    viewModel.setBiometricEnabled(false)  // ← just disable
                }
            }
        )
    }
}
