package com.example.mimi_projet_zentech.ui.theme.ui.signIn.passwordConfirmation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.ProfileUser
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.dispalyUserName
import com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount.loginButton
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInInput
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInState
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInViewModel
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignIncon
import com.example.mimi_projet_zentech.ui.theme.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pass_confirmScrren(  navController: NavController , viewModel: passConfirmationViewModel = viewModel () , email :String? ) {
    val field by viewModel.fields.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(email) {
        viewModel.loadUser(email?:"")           //  get from Room
    }

    LaunchedEffect(Unit) {
        viewModel.onLoginSuccess = {
            navController.navigate(Screen.Home.getRoute()) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = { Text("", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {

                        Icon(

                            Icons.Default.ArrowBack, "Back",
                            tint = MaterialTheme.colorScheme.onBackground,

                        )
                    }
                },

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
            Spacer(Modifier.height(30.dp))
            ProfileUser(80)
            Spacer(modifier = Modifier.height(7.dp))

           // user name dispaly get dta from Room
            dispalyUserName(user?.name ?: "")

            Spacer(modifier = Modifier.height(38.dp))
            SignInInput(
                title = "password",
                value = field.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                icon = SignIncon.Password,
                isPasswordField = true,
                errorMessage = field.passwordError,
                enableField = state !is SignInState.Loading
            )

            if (state is SignInState.Error) {
                Text(
                    text = (state as SignInState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(38.dp))
            loginButton(
                to_login = { viewModel.onConfirmClicked() },  // call login  to  HomeScrren
                isLoading = state is SignInState.Loading
            )

            Spacer(modifier = Modifier.height(38.dp))
            TextButton(
                onClick = {},
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = SignInStrings.FORGET_PASSWORD,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}



