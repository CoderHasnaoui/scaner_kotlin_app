package com.example.mimi_projet_zentech.ui.theme.ui.signIn

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.ui.theme.data.repository.SignInRepository
import com.example.mimi_projet_zentech.ui.theme.ui.homePage.HomeScreen
import com.example.mimi_projet_zentech.ui.theme.util.LoginResult
import kotlinx.coroutines.launch

class  SignInViewModel : ViewModel(){
    private  val repo  = SignInRepository()
    var email by mutableStateOf("")
        private set //
    var loginMessage by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun onEmailChanged(newValue: String) {
        email = newValue
    }
    fun onPasswordChanged(newValue: String) {
        password = newValue
    }
    var navigateToHome by mutableStateOf(false) // for know status of Home Page
        private set
/* those Variable for check email and password  */
    var emailError by mutableStateOf<String?>(null)
    private set
    var passwordError by mutableStateOf<String?>(null)
        private set
     fun onSignInClicked() {
         emailError = null
         passwordError = null
         // email validation
         if (email.isEmpty()) {
             emailError = "Field is required"
         } else if (!email.contains("@")) {
             emailError = "Email must contain @"
         }
//         Password Validation
         if (password.isEmpty()) {
             passwordError = "Field is required"
         } else if (password.length < 6) {
             passwordError = "Password must be at least 6 characters"
         }
//        check Error from filed
         if (emailError == null && passwordError == null) {
        viewModelScope.launch {
            isLoading = true
            loginMessage = ""
        val result = repo.login(email, password)
        when (result) {
            is LoginResult.Success -> {
//                loginMessage = result.message
                navigateToHome = true
            }
            is LoginResult.Error -> {
                loginMessage = result.errorMessage
            }
            LoginResult.Loading -> { }
        }
            isLoading = false
            }
             }
    }
    fun onNavigationDone() {
        navigateToHome = false
    }
}
