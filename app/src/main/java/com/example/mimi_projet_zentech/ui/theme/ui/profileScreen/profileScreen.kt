package com.example.mimi_projet_zentech.ui.theme.ui.profileScreen
import android.content.Context
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.ui.theme.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.ui.theme.data.repository.AuthRepository
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository

val repo = HomeRepository()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileScreen(
    navController: NavController,
    isDarkModeState: MutableState<Boolean>, // This controls the app theme
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences(SignInStrings.PRE_LOGGIN_NAME, Context.MODE_PRIVATE) }
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    // Trigger API load once
    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
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
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                val merchant = viewModel.selectedMerchant
                                Column {
                                    Text(merchant?.name ?: "No Selection", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    val tag = merchant?.name?.let { "@${it.replace(" ", "").lowercase()}" } ?: "@unknown"
                                    Text(tag, fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }

                        Button(

                            onClick = { viewModel.tokenManager.clearSelectedSlug()
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

                    if (!viewModel.isLoading && viewModel.selectedMerchant != null) {
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
                        sharedPref.edit().putBoolean("is_dark_mode", newValue).apply() // Persists it
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- 4. Log Out ---
            Button(
                onClick = {
                    viewModel.tokenManager.clearToken()
                    sharedPref.edit().putBoolean(SignInStrings.PRE_IS_LOGGED_IN, false).apply()
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
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