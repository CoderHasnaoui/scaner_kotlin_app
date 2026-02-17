package com.example.mimi_projet_zentech.ui.theme.ui.signIn

import android.app.Activity
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.ErrorRed
import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.util.Screen

@Composable
fun SignInScrenn(viewModel: SignInViewModel = viewModel() , navController: NavController) {
    // Access the window to control the status bar

    val focusManager = LocalFocusManager.current
    if (viewModel.navigateToHome) {

        var userDataWithRoute = Screen.Home.fullRoute(viewModel.email)
        navController.navigate(userDataWithRoute) {
            popUpTo(Screen.Login.route) { inclusive = true }
        }
        viewModel.onNavigationDone()
    }
//    var email by remember{mutableStateOf("")}
//    var password by remember{mutableStateOf("")}




            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)// Only this screen
                .padding(20.dp)
                .padding(vertical = 30.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus() // Hides keyboard when you tap the background
                    })
                }


                ,){
                Spacer(Modifier.height(24.dp))
                Text(text = SignInStrings.SIGN_IN_TITLE,
                    style = MaterialTheme.typography.headlineMedium ,
                    color = MaterialTheme.colorScheme.onBackground

                )
                Spacer(Modifier.height(20.dp))
                Text(text = SignInStrings.WELCOME_BACK ,
                    style = MaterialTheme.typography.bodySmall ,
                    fontWeight = FontWeight.W100 ,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                // this is Eamail Filed
                SignInInput("email" ,viewModel.email ,onValueChange = {viewModel.onEmailChanged(it)}  , icon = SignIncon.Email , errorMessage = viewModel.emailError )
                // this is Password Field
                SignInInput("password" ,viewModel.password ,onValueChange = {viewModel.onPasswordChanged(it)}  , icon = SignIncon.Password , isPasswordField = true  , errorMessage = viewModel.passwordError)
                if (viewModel.loginMessage.isNotEmpty()) {
                    Text(
                        text = viewModel.loginMessage,
                        color = if (viewModel.loginMessage.contains("Welcome")) Color.Green else Color.Red,
//                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                // this is Forget password tache  ?
                TextButton(onClick = {} ,
                    contentPadding = PaddingValues(0.dp) // that for forcing to padding to back to value default  ?
                ) {

                    Text(
                        text = SignInStrings.FORGET_PASSWORD ,
                        color = Color.Gray ,
                        style = MaterialTheme.typography.bodySmall ,
                        textDecoration = TextDecoration.Underline
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                // Sign In Button
                TextButton(  {viewModel.onSignInClicked()},

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp) ,
                    colors = ButtonDefaults.buttonColors( containerColor = Color(0xFF0452F0))
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center ,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(SignInStrings.SIGN_IN_BUTTON , style = MaterialTheme .typography.labelLarge , color = Color.White )
                            Spacer(modifier = Modifier.width(20.dp))
                            Icon(
                                imageVector = SignIncon.ArrowRight,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }}
                }


                Row(

                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center ,

                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text =SignInStrings.FORGET_PASSWORD,
                        color = Color.Gray
                    )
                    Text(
                        text = SignInStrings. SIGN_UP ,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                }
        }




}

