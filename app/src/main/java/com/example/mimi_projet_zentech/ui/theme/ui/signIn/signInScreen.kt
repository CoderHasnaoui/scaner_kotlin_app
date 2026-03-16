package com.example.mimi_projet_zentech.ui.theme.ui.signIn

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.util.Screen

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("ContextCastToActivity")
@Composable
fun SignInScrenn(viewModel: SignInViewModel = viewModel() , navController: NavController) {

    val focusManager = LocalFocusManager.current
    val activity = LocalContext.current as FragmentActivity

    val state by viewModel.state.collectAsStateWithLifecycle()
    val field by viewModel.fields.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onLoginSuccess = {
            navController.navigate(Screen.Home.getRoute()) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    // ---  Main Screen ---
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)// Color dynamique  with all screen
                .padding(20.dp)
                .padding(vertical = 30.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus() // Hides keyboard when I tap the background
                    })
                }
                ){
                if (state is SignInState.ShowBiometricDialog) {
                    AlertDialog(
                        onDismissRequest = { viewModel.onBiometricDialogResult(false, activity) },
                        title = { Text("Enable Fingerprint Login?") },
                        text = { Text("Use your fingerprint to log in faster next time.") },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.onBiometricDialogResult(true, activity)
                            }) {
                                Text("Enable", color = Color(0xFF0452F0))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                viewModel.onBiometricDialogResult(false, activity)
                            }) {
                                Text("Skip", color = Color.Gray)
                            }
                        }
                    )
                }
                Spacer(Modifier.height(24.dp))
                // --- Sign in Text ---
                Text(text = SignInStrings.SIGN_IN_TITLE,
                    style = MaterialTheme.typography.headlineMedium ,
                    color = MaterialTheme.colorScheme.onBackground

                )

                Spacer(Modifier.height(20.dp))
                // --- Welcom Back Text ---
                Text(text = SignInStrings.WELCOME_BACK ,
                    style = MaterialTheme.typography.bodySmall ,
                    fontWeight = FontWeight.W100 ,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))
                // --- Email Text   ---
                SignInInput("email" ,field.email ,onValueChange = {viewModel.onEmailChanged(it)}  , icon = SignIncon.Email , errorMessage = field.emailError  , enableField = state !is SignInState.Loading)
                // --- Password Field  ---
                SignInInput("password" ,field.password ,onValueChange = {viewModel.onPasswordChanged(it)}  , icon = SignIncon.Password , isPasswordField = true  , errorMessage = field.passwordError , enableField =state !is SignInState.Loading)
                // Eroor message by selad Status
                    if (state is SignInState.Error) {
                        Text(
                            text = (state as SignInState.Error).message,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
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
                TextButton(  onClick = {viewModel.onSignInClicked()},
                    enabled = state !is SignInState.Loading,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp) ,
                    colors = ButtonDefaults.buttonColors( containerColor = Color(0xFF0452F0))
                ) {
                    if (state is SignInState.Loading) {
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


