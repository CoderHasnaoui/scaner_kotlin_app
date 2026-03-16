package com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount


import HomeViewModel
import android.annotation.SuppressLint
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import com.example.mimi_projet_zentech.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.times
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount
import com.example.mimi_projet_zentech.ui.theme.util.Screen

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AccountScreen(navController: NavController ,    viewModel: AccountViewModel = viewModel()) {

    val accountState by viewModel. accountState.collectAsStateWithLifecycle()
    when(accountState){
        is AccountUiState.NoAccount ->{

            LaunchedEffect(accountState) {
                if(accountState is AccountUiState.NoAccount){
                navController.navigate(Screen.Login.route){
                   popUpTo (Screen.Account.route){inclusive = true  }
                }

            }}
        }

        is AccountUiState.SingleAccount ->{
            val user = (accountState as AccountUiState.SingleAccount).user
                singleAccountUi(navController , user)
        }
        is AccountUiState.MultipleAccounts ->{
            val users  = (accountState as AccountUiState.MultipleAccounts).users
            multipleAccount(navController ,users)}

        AccountUiState.Loading -> {}
    }


}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun singleAccountUi(navController: NavController , user: UserAccount ) {



   val  viewModel: AccountViewModel = viewModel()
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onLoginSuccess = {
            navController.navigate(Screen.Home.getRoute()) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    LaunchedEffect(isBiometricEnabled) {
        Log.d("BIOMETRIC", "isBiometricEnabled = $isBiometricEnabled")
    }

    LaunchedEffect(loginError) {
        if (loginError) {
            navController.navigate(Screen.passwordConfirm.getRoute(user.email))
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        // horizental icon for delet Acounts
        IconButton(
            onClick = {  navController.navigate(Screen.ManageProfile.getRoute(user.email)) },
            modifier = Modifier
                .align(Alignment.End)
                .padding(WindowInsets.statusBars.asPaddingValues())
//                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "Menu"
            )
        }
        // image of app
        iconApp()

        Spacer(modifier = Modifier.height(7.dp))
        // profile image
        ProfileUser()
        Spacer(modifier = Modifier.height(7.dp))
        // usser name
        dispalyUserName(user.name)
        Spacer(modifier = Modifier.height(38.dp))
        // log with this Account
        loginButton(


                to_login = {

                    if (isBiometricEnabled) {
                        when (viewModel.checkBiometricSupport(context)) {

                            BiometricCheckResult.Available -> {
                                viewModel.launchBiometric(
                                    activity = activity,
                                    user = user,
                                    onFallback = {
                                        navController.navigate(Screen.passwordConfirm.getRoute(user.email))
                                    }
                                )
                            }

                            BiometricCheckResult.NotEnrolled -> {
                                // send user To Seting to make Fingerprint
                                val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                                context.startActivity(intent)
                            }

                            BiometricCheckResult.NotSupported -> {
                                // the phone has no sensor  so fo to PAss Screen
                                navController.navigate(Screen.passwordConfirm.getRoute(user.email))
                            }
                        }
                    } else {
                        navController.navigate(Screen.passwordConfirm.getRoute(user.email))
                    }
                }
            )


        Spacer(Modifier.height(7.dp))
        //Login Other Account
        toLoginPage(to_login = { navController.navigate(Screen.Login.route) })
        Spacer(modifier = Modifier.weight(1f))
        // create nem account
        // still not Avaliable
        createNew()
        Spacer(modifier = Modifier.height(12.dp))
        brandName()
        Spacer(modifier = Modifier.height(10.dp))
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
fun multipleAccount(navController: NavController ,users :  List<UserAccount>) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp




    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding() // 🔹out of barr
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Manage Account Menu
        IconButton(
            onClick = { },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "Menu",
                tint = Color(0xFF1C1E21)
            )
        }

        Spacer(modifier = Modifier.height(screenHeight.value*0.1.dp))
        // app icon
        iconApp()

        Spacer(modifier = Modifier.height(screenHeight.value*0.1.dp))

        //  List Of Account And Log to otherAccount
        ListOfAccount(users , Modifier.weight(1f))
        Spacer(modifier = Modifier.height(12.dp))
        brandName()
        Spacer(modifier = Modifier.height(10.dp))
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}


