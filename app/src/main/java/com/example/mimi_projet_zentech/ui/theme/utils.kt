package com.example.mimi_projet_zentech.ui.theme

import ScannerScreen
import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.ui.splash.SplashScreen
import com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen.DeniedScreen
import com.example.mimi_projet_zentech.ui.theme.ui.homePage.HomeScreen
import com.example.mimi_projet_zentech.ui.theme.ui.profileScreen.profileScreen
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInScrenn
import com.example.mimi_projet_zentech.ui.theme.ui.statusScreen.ValidScreen
import com.example.mimi_projet_zentech.ui.theme.util.NAV_ARG_EMAIL
import com.example.mimi_projet_zentech.ui.theme.util.Screen
import com.yourapp.qrscanner.permission.CameraPermission

@Composable
fun NavigationsSeting (isDarkModeState: MutableState<Boolean>){
    val context = LocalContext.current

    val navController = rememberNavController()
    val isLoggedIn = remember {
        val sharedPref = context.getSharedPreferences(SignInStrings.PRE_LOGGIN_NAME, Context.MODE_PRIVATE)
        sharedPref.getBoolean(SignInStrings.PRE_IS_LOGGED_IN, false)
    }

    NavHost(navController = navController , startDestination = Screen.Splash.route) {
        composable (Screen.Splash.route) {
            SplashScreen(navController = navController , isLoggedIn)
        }

        composable(
            route =  Screen.Login.route,
            enterTransition = {
                fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(1000)
                        )
            }
        ) {
            SignInScrenn(navController=navController)
        }

        composable(Screen.Home.route , arguments = listOf(navArgument(NAV_ARG_EMAIL) {
            type = NavType.StringType
        }))
        {it->
            val  email = it.arguments?.getString(NAV_ARG_EMAIL)

            HomeScreen(navController , email)

        }
        composable(Screen.ScannerScreen.route ,
            arguments = listOf(navArgument("buisnesIs"){
            type = NavType.IntType
        })) {  it->
            val businessId =  it.arguments?.getInt("buisnesIs")
            CameraPermission(
                businessId ,
                onGranted = { ScannerScreen(navController , businessId ) } ,
                navController
            )


        }
        composable(
            route = Screen.DeniedScreen.route, // matches "denied/{buisnesIs}"
            arguments = listOf(navArgument("buisnesIs") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val businessId = backStackEntry.arguments?.getInt("buisnesIs")
            DeniedScreen(navController, businessId)
        }
        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("buisnesIs") { type = NavType.IntType })
        ) { backStackEntry ->
            val businessId = backStackEntry.arguments?.getInt("buisnesIs")
            profileScreen(navController = navController, businessId = businessId   , isDarkModeState)
        }
//        composable(Screen.ScanStatus.route){
//            ValidScreen(navController = navController )
//        }

        composable (Screen.ScanRes.route , arguments = listOf(navArgument("buisnisId")
        {
            type = NavType.IntType
        } , navArgument("ticketNum"){
            type = NavType.StringType
        }, navArgument("scanRes"){
            type = NavType.StringType
        }
        )){ it->
            val businessId =  it.arguments?.getInt("buisnisId")
            val ticketNum  = it.arguments?.getString("ticketNum")
            val resString = it.arguments?.getString("scanRes") ?: "NOT_FOUND"
            // Convert String back to Enum
            val status = ScanStatus.valueOf(resString)
            ValidScreen(navController = navController  , buisnesId = businessId , ticketNum = ticketNum , scanStatus = status)

        }
    }
}