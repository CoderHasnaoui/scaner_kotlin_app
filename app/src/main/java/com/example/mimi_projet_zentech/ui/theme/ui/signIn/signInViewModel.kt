package com.example.mimi_projet_zentech.ui.theme.ui.signIn

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.ui.theme.SignInStrings
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.data.model.Login.LoginRequest
import com.example.mimi_projet_zentech.ui.theme.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.ui.theme.data.repository.AuthRepository
import com.example.mimi_projet_zentech.ui.theme.data.repository.SignInRepository
import com.example.mimi_projet_zentech.ui.theme.util.LoginResult
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.jvm.java

class  SignInViewModel (application: Application): AndroidViewModel(application) {


    private val tokenManager = TokenManager(getApplication())
    private val authApi = RetrofitInstance.publicApi
    private val repository = AuthRepository(authApi)

    private val userRepository = UserRepository(getApplication<Application>().dataStore)

    //    private val  repository  = AuthRepository()
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
            if (!isNetworkAvailable()) {
                loginMessage = "No internet connection."
                return
            }
            viewModelScope.launch {
                isLoading = true
                loginMessage = ""
                try {


                    val request = LoginRequest(
                        email = email,
                        password = password,
                        gToken = "",

                        platform = "android",
                        rememberMe = true
                    )
                    val response = repository.login(request)
                    if (response.isSuccessful) {
                        val data = response.body()
                        val token = data?.token
                        val name = data?.user?.name?:"unknow "
                        val emailApi  = data?.user?.email?:"exemple@gmail.com"
//                        val email = data?.user?.email?:"unknow "
                        if (token != null) {
//                    tokenManager.saveToken(token)
                            tokenManager.saveToken(token)

                            viewModelScope.launch {
                                userRepository.saveUserInfo(name , emailApi)
                            }

                            loginMessage = "Loading ....."
                            navigateToHome = true
                        } else {
                            loginMessage = "Server returned no token."
                        }

                    } else {

                        val errorText = response.errorBody()?.string()

                        if (errorText?.contains("did not match our records") == true) {
                            loginMessage = "Email or password is invalid , Please try again."
                        } else {
                            loginMessage = "Login failed. Please try again."
                        }
                    }
                } catch (e: Exception) {
                    loginMessage = "Something Wrong"
                } finally {
                    isLoading = false
                }
//        val result = repo.login(email, password)
//            val result = repository
//        when (result) {
//            is LoginResult.Success -> {
//                val sharedPref = getApplication<Application>().getSharedPreferences(SignInStrings.PRE_LOGGIN_NAME, Context.MODE_PRIVATE)
//                with(sharedPref.edit()) {
//                    putBoolean(SignInStrings.PRE_IS_LOGGED_IN, true)
//                    // You can also save the user's email if you want to show it on the Profile screen later
//                    putString(SignInStrings.PRE_USER_EMAIL, email)
//                    putString(SignInStrings.PRE_USER_PASSWORD, email)
//                    apply()
//                }
////                loginMessage = result.message
//                navigateToHome = true
//            }
//            is LoginResult.Error -> {
//                loginMessage = result.errorMessage
//            }
//            LoginResult.Loading -> { }
//        }
//            isLoading = false
//            }
            }
        }

    }
    fun onNavigationDone() {
        navigateToHome = false
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getApplication<Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

}
