package com.example.mimi_projet_zentech.ui.theme

import ScannerScreen
import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mimi_projet_zentech.data.local.SessionManager

import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.ui.splash.SplashScreen
import com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen.DeniedScreen
import com.example.mimi_projet_zentech.ui.theme.ui.homePage.HomeScreen
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.AccountScreen
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.ProfileManager.ManageAccountsScreen
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.ProfileManager.ManageProfileScreen
import com.example.mimi_projet_zentech.ui.theme.ui.profileScreen.profileScreen
import com.example.mimi_projet_zentech.ui.theme.ui.scanScreen.ScanViewMode
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInScrenn
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.passwordConfirmation.Pass_confirmScrren
import com.example.mimi_projet_zentech.ui.theme.ui.statusScreen.ValidScreen
import com.example.mimi_projet_zentech.ui.theme.util.Screen
import com.yourapp.qrscanner.permission.CameraPermission

@SuppressLint("RememberReturnType")
@Composable
fun NavigationsSeting (isDarkModeState: MutableState<Boolean>){
    val context = LocalContext.current
    val sharedViewModel : ScanViewMode = viewModel ()
    val navController = rememberNavController()
//    val tokenManager = remember { TokenManager(context) }
//    val isLoggedIn = remember { tokenManager.isLoggedIn() }
    remember {
        SessionManager.onTokenExpired = {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
//    LaunchedEffect(Unit) {
//        SessionManager.onTokenExpired = {
//            navController.navigate(Screen.Login.route) {
//                popUpTo(0) { inclusive = true }
//            }
//        }
//    }

    NavHost(navController = navController , startDestination = Screen.Splash.route) {
        composable (Screen.Splash.route) {
            SplashScreen(
                navController = navController,

            )
        }
        composable(Screen.Account.route) {
            AccountScreen(navController = navController)
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

        composable(Screen.Home.route , arguments = listOf(navArgument("forceSelect") {
            type = NavType.BoolType
        }))
        {it->
            val  forceSelected = it.arguments?.getBoolean("forceSelect")?:false

            HomeScreen(navController , forceSelected)

        }

        composable(Screen.passwordConfirm.route , arguments = listOf(navArgument("email") {
            type = NavType.StringType
        }))
        {it->
            val  email = it.arguments?.getString("email")?: ""

            Pass_confirmScrren(navController , email = email)

        }

        composable(Screen.ManageProfile.route , arguments = listOf(navArgument("email") {
            type = NavType.StringType
        }))
        {it->
            val  email = it.arguments?.getString("email")?: ""

            ManageProfileScreen(navController , email = email)

        }

        composable(Screen.ScannerScreen.route) {
            CameraPermission(

                onGranted = { ScannerScreen(navController  , sharedViewModel) } ,
                navController
            )
        }
//        composable(Screen.ScannerScreen.route ,
//            arguments = listOf(navArgument("slug"){
//            type = NavType.StringType
//        })) {  it->
//            val slug =  it.arguments?.getString("slug")
//            CameraPermission(
//                slug ,
//                onGranted = { ScannerScreen(navController , slug ) } ,
//                navController
//            )
//
//
//        }
        composable(
            route = Screen.DeniedScreen.route, // matches "denied/{buisnesIs}"
//            arguments = listOf(navArgument("slug") {
//                type = NavType.StringType
//            }
//            )
        ){
//        { backStackEntry ->
//            val slug = backStackEntry.arguments?.getInt("slug")
            DeniedScreen(navController , sharedViewModel)
        }

        composable (Screen.Profile.route){
            profileScreen(navController , isDarkModeState)
        }
        composable (Screen.ManageAccounts.route){
            ManageAccountsScreen(navController)
        }
//        composable(
//            route = Screen.Profile.route,
//            arguments = listOf(navArgument("buisnesIs") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val businessId = backStackEntry.arguments?.getInt("buisnesIs")
//            profileScreen(navController = navController, businessId = businessId   , isDarkModeState)
//        }
//        composable(Screen.ScanStatus.route){
//            ValidScreen(navController = navController )
//        }

//        composable (Screen.ScanRes.route){
//                        ValidScreen(navController = navController   , ticketNum = ticketNum , scanStatus = status)
//
//        }
        composable (Screen.ScanRes.route , arguments = listOf(

            navArgument("ticketNum"){
            type = NavType.StringType
        }, navArgument("scanRes"){
            type = NavType.StringType
        }
        )){
            val ticketNum  = it.arguments?.getString("ticketNum")
            val resString = it.arguments?.getString("scanRes") ?: "NOT_FOUND"
            // Convert String back to Enum
            val status = ScanStatus.valueOf(resString)
            ValidScreen(navController = navController , scanStatusInitial = status , ticketNum = ticketNum )

        }
    }
}