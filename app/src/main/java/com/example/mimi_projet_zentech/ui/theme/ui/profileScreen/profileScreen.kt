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

import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository

val repo = HomeRepository()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileScreen(navController: NavController, businessId: Int?,isDarkModeState: MutableState<Boolean> ) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences(SignInStrings.PRE_LOGGIN_NAME, Context.MODE_PRIVATE) }

    // Read saved data
    val userEmail = sharedPref.getString(SignInStrings.PRE_USER_EMAIL, "User@email.com") ?: ""

    // 1. Safely get the business data
    val buisnesGroupe = remember(businessId) { repo.getBuisnesById(businessId) }
    val name = buisnesGroupe?.name ?: "Unknown Business"
    val offices = buisnesGroupe?.offices ?: emptyList()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
          topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface),
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
            // --- 1. Info Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0XFFF4F4F5), CircleShape), // Added CircleShape
                        contentAlignment = Alignment.Center
                    ) {
                        Text("OO", fontSize = 20.sp, fontWeight = FontWeight.Bold , color = Color(0xFF121212))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Abdelilah Hasnaoui", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(userEmail, color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Your selected group", modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // --- 2. Business Group Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0x68FAFAFB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Icon(Icons.Default.Business, null, modifier = Modifier.padding(10.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            // Safe tag generation
                            val tag = "@${name.replace(" ", "").lowercase()}"
                            Text(tag, fontSize = 14.sp, color = Color.Gray)
                        }

                        Button(
                            onClick = {
                                // Important: Home route needs the email argument!
                                navController.navigate(Screen.Home.fullRoute(userEmail)) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1851C5))
                        ) {
                            Text("Change", color = Color.White)
                        }
                    }

                    if (offices.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                offices.forEach { office ->
                                    Text(text = office, color = MaterialTheme.colorScheme.onSurface , fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. Dark Mode ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode", fontWeight = FontWeight.Medium)
                Switch(

                    checked = isDarkModeState.value,
                    onCheckedChange = { newValue ->
                        isDarkModeState.value = newValue
                        sharedPref.edit().putBoolean("is_dark_mode", newValue).apply()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- 4. Log Out ---
            Button(
                onClick = {
                    sharedPref.edit().putBoolean(SignInStrings.PRE_IS_LOGGED_IN, false).apply()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
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