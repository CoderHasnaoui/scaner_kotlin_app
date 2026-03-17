package com.example.mimi_projet_zentech.ui.theme.ui.signIn.passwordConfirmation

import android.app.Application
import android.content.Context
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.local.db.DatabaseProvider
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount
import com.example.mimi_projet_zentech.data.model.Login.LoginRequest
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.AuthRepository
import com.example.mimi_projet_zentech.data.repository.UserAccountRepository
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.SignInState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class passConfirmationViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)
    private val roomRepo by lazy { UserAccountRepository(db.userAccountDao()) }
    private val tokenManager = TokenManager(application)
    private val repository = AuthRepository(RetrofitInstance.publicApi)

    var onLoginSuccess: (() -> Unit)? = null

    private val _state = MutableStateFlow<SignInState>(SignInState.Ready)
    val state: StateFlow<SignInState> = _state.asStateFlow()

    private val _fields = MutableStateFlow(PassConfirmData())
    val fields: StateFlow<PassConfirmData> = _fields.asStateFlow()

    private val _user = MutableStateFlow<UserAccount?>(null)
    val user: StateFlow<UserAccount?> = _user.asStateFlow()

    fun loadUser(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val account = roomRepo.getUserByEmail(email)
            _user.value = account
        }
    }

    fun onPasswordChanged(newValue: String) {
        _fields.update { it.copy(password = newValue.trim(), passwordError = null) }
    }

    fun onConfirmClicked() {
        val current = _fields.value
        val user = _user.value ?: return

        val passwordErr = when {
            current.password.isEmpty() -> "Field is required"
            current.password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }

        if (passwordErr != null) {
            _fields.update { it.copy(passwordError = passwordErr) }
            return
        }

        if (!isNetworkAvailable()) {
            _state.value = SignInState.Error("No internet connection.")
            return
        }

        viewModelScope.launch {
            _state.value = SignInState.Loading
            try {
                val request = LoginRequest(
                    email = user.email,
                    password = current.password,
                    gToken = "",
                    platform = "android",
                    rememberMe = true
                )
                val response = repository.login(request)
                if (response.isSuccessful) {
                    val data = response.body()
                    val token = data?.token
                    if (token != null) {
                        tokenManager.saveToken(token)
                        tokenManager.setLoggedIn(true)
                        withContext(Dispatchers.IO) {
                            roomRepo.updateLastSession(user.email, System.currentTimeMillis())
                        }
                        onLoginSuccess?.invoke()
                    } else {
                        _state.value = SignInState.Error("Invalid credentials")
                    }
                } else {
                    val errorText = response.errorBody()?.string() ?: ""
                    _state.value = if ("did not match our records" in errorText)
                        SignInState.Error("Password incorrect")
                    else
                        SignInState.Error("Login failed. Please try again.")
                }
            } catch (e: Exception) {
                _state.value = SignInState.Error("Something went wrong.")
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getApplication<Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}

data class PassConfirmData(
    val password: String = "",
    val passwordError: String? = null
)