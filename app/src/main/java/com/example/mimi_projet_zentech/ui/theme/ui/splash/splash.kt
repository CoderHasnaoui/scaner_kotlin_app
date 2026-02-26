package com.example.mimi_projet_zentech.ui.theme.ui.splash

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.mimi_projet_zentech.R
import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.util.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController , isLoggedIn : Boolean) {
    var context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.scanner_animation))
    val progress by animateLottieCompositionAsState(composition = composition)
    var dotCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(progress) {
        if (progress == 1f) {
            if (isLoggedIn) {
                // Get the saved email to pass to Home
                val sharedPref = context.getSharedPreferences(SignInStrings.PRE_LOGGIN_NAME, Context.MODE_PRIVATE)
                val email = sharedPref.getString(SignInStrings.PRE_USER_EMAIL, "") ?: ""
                var userDataWithRoute = Screen.Home.getRoute()
                navController.navigate(userDataWithRoute) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }

            } else {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
    }
    // 2. Animate the counter from 0 to 3

    LaunchedEffect(Unit) {
        while (true) {
            delay(300)
            dotCount = (dotCount + 1) % 4
        }
    }
//    if (isLoggedIn) {
//        navController.navigate(Screen.Home.route) {
//            popUpTo(Screen.Splash.route) { inclusive = true }
//        }
//    } else {
//        // Go to Login and delete Splash from history
//        navController.navigate(Screen.Login.route) {
//            popUpTo(Screen.Splash.route) { inclusive = true }
//        }
//    }
    // The Gradient Box
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1B59D7),
                        Color(0xFF08215E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
         // Lottie Animation
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )
//           Animated Text with Dots
           Text(
                text = "Tickets scanner" + ".".repeat(dotCount),
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 20.sp

            )
        }
    }
}
