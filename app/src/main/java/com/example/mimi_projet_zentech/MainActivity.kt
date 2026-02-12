package com.example.mimi_projet_zentech

import ScannerScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val insetsController =   WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true

            NavigationsSeting() // this is get the Sign In scereen
//            ScanPageScreen(insetsController)  // this is give the Scan of Buisnes  Screen
//            CameraPermission {
//                ScannerScreen(insetsController)
//            }

        }
    }
}
@Composable
fun NavigationsSeting (){
    val navController = rememberNavController()
    NavHost(navController = navController , startDestination = Screen.Splash.route) {
        composable (Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable (Screen.Login.route) {

          SignInScrenn(navController = navController)
        }


        composable(Screen.Home.route ,
            arguments = listOf(navArgument(NAV_ARG_EMAIL ){
type= NavType.StringType
            })
        )
        {it->
            val  email = it.arguments?.getString(NAV_ARG_EMAIL)

               HomeScreen(navController , email)

        }
        composable(Screen.ScannerScreen.route , arguments = listOf(navArgument("buisnesIs"){
            type = NavType.IntType
        })) {  it->
            val businessId =  it.arguments?.getInt("buisnesIs")
            CameraPermission(
                businessId ,
                onGranted = { ScannerScreen(navController , businessId ) } ,
                navController
            )


        }
        composable (Screen.DeniedScreen.route ){
            DeniedScreen(navController)
        }
        composable (Screen.Profile.route) {
            profileScreen(navController = navController)
        }
        composable(Screen.ScanStatus.route){
            ValidScreen(navController = navController )
        }
    }
}
@Composable
fun SplashScreen(
    navController: NavController
) {
    // Navigate after X seconds
    LaunchedEffect(Unit) {
        delay(3000) // 3 seconds
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Login.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            PulsingLogo()

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedDotsText(text = "Ticket Scanner")
        }
    }
}

@Composable
fun AnimatedDotsText(
    text: String,
    modifier: Modifier = Modifier,
    maxDots: Int = 3,
    delayMillis: Long = 400
) {
    var dots by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            dots = (dots + 1) % (maxDots + 1)
            delay(delayMillis)
        }
    }

    Text(
        text = text + ".".repeat(dots),
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PulsingLogo() {
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Image(
        painter = painterResource(id = R.drawable.splash_icon),
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
    )
}

