package com.example.mimi_projet_zentech

import ScannerScreen
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.mimi_projet_zentech.ui.theme.MimiprojetzentechTheme
import com.example.mimi_projet_zentech.ui.theme.NavigationsSeting
import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen.DeniedScreen
import com.example.mimi_projet_zentech.ui.theme.ui.homePage.HomeScreen
import com.example.mimi_projet_zentech.ui.theme.ui.profileScreen.profileScreen
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInScrenn
import com.example.mimi_projet_zentech.ui.theme.ui.statusScreen.ValidScreen
import com.example.mimi_projet_zentech.ui.theme.util.NAV_ARG_EMAIL
import com.example.mimi_projet_zentech.ui.theme.util.Screen
import com.yourapp.qrscanner.permission.CameraPermission
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
// This is okay, it lets Compose draw behind the bars

   installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPref = getSharedPreferences(SignInStrings.PRE_LOGGIN_NAME, Context.MODE_PRIVATE)
//        val isDarkStored = sharedPref.getBoolean("is_dark_mode", false)

        setContent {
            val isDarkMode = remember {
                mutableStateOf(sharedPref.getBoolean("is_dark_mode", false))
            }
//            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
//            insetsController.isAppearanceLightStatusBars = false
//            insetsController.isAppearanceLightNavigationBars = false

            MimiprojetzentechTheme(darkTheme = isDarkMode.value){
            NavigationsSeting(isDarkMode)
        }
        }
    }
}





