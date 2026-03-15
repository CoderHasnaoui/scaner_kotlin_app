package com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.db.DatabaseProvider
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount
import com.example.mimi_projet_zentech.data.repository.UserAccountRepository
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.UserRepository
import com.example.mimi_projet_zentech.ui.theme.util.Screen

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.model.Login.LoginRequest
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.AuthRepository
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.crypto.Cipher

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    val tokenManager = TokenManager(application)
    private val db = DatabaseProvider.getDatabase(application)
    private val roomRepo by lazy { UserAccountRepository(db.userAccountDao()) }
    private val userRepository = UserRepository(getApplication<Application>().dataStore)

    // Account State
    private val _loginError = MutableStateFlow(false)
    val loginError: StateFlow<Boolean> = _loginError.asStateFlow()
    private val _accountState = MutableStateFlow<AccountUiState>(AccountUiState.Loading)
    val accountState: StateFlow<AccountUiState> = _accountState.asStateFlow()
    private val _biometricReady = MutableStateFlow(false)
    val biometricReady: StateFlow<Boolean> = _biometricReady.asStateFlow()
    // Biometric State
    private val _biometricState = MutableStateFlow<BiometricState>(BiometricState.Idle)
    val biometricState: StateFlow<BiometricState> = _biometricState.asStateFlow()
    private val repository = AuthRepository(RetrofitInstance.publicApi)  // ← add
    var onLoginSuccess: (() -> Unit)? = null
    // Biometric enabled flag
    val isBiometricEnabled: StateFlow<Boolean> = userRepository
        .isBiometricEnabled()

        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepo.allUsers.collect { users ->
                _accountState.value = when {
                    users.isEmpty() -> AccountUiState.NoAccount
                    users.size == 1 -> AccountUiState.SingleAccount(users.first())
                    else -> AccountUiState.MultipleAccounts(users)
                }
            }
        }
    }

    // biometrik

    fun checkBiometricSupport(context: Context): BiometricCheckResult {
        val manager = BiometricManager.from(context)
        return when (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricCheckResult.Available

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricCheckResult.NotEnrolled

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricCheckResult.NotSupported

            else -> BiometricCheckResult.NotSupported
        }
    }

    fun launchBiometric(
        activity: FragmentActivity,
        user: UserAccount,
        onSuccess: (Cipher) -> Unit,
        onFallback: () -> Unit
    ) {
        val ivString = user.passwordIv ?: run { onFallback(); return }
        val cipher = tokenManager.getDecryptCipher(ivString)  // ← IV from Room
        _biometricState.value = BiometricState.Authenticating

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    _biometricState.value = BiometricState.Success
                    val authenticatedCipher = result.cryptoObject?.cipher!!
                    onSuccess(authenticatedCipher)  // ← pass cipher back
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    _biometricState.value = BiometricState.Idle
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_CANCELED -> {
                            // tapped outside → do nothing → stay on screen
                        }
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            // clicked "Use password instead"
                            onFallback()
                        }
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            // too many attempts
                            onFallback()
                        }
                        else -> {
                            onFallback()
                        }
                    }

                }

                override fun onAuthenticationFailed() {
                    // wrong finger → Android handles retry automatically
                    // no need to do anything here
                    _biometricState.value = BiometricState.Idle
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login as ${user.name}")
            .setSubtitle("Use your fingerprint to continue")
            .setNegativeButtonText("Use password instead")
            .setConfirmationRequired(false)  // ← add this

            .build()

        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login as ${user.name}")
                .setSubtitle("Use your fingerprint to continue")
                .setNegativeButtonText("Use password instead")
                .setConfirmationRequired(true)
                .build(),
            BiometricPrompt.CryptoObject(cipher)  // ← required!
        )
    }

    // ── Remove User ────────────────────────────────────────
    fun loginWithBiometric(email: String, password: String) {
        viewModelScope.launch {
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
                    val token = response.body()?.token
                    if (token != null) {
                        tokenManager.saveToken(token)
                        tokenManager.setLoggedIn(true)
                        onLoginSuccess?.invoke()
                    }
                }else{
                    _loginError.value=true

                }
            } catch (e: Exception) {
                _loginError.value = true
            }
        }
    }
    fun removeUser(user: UserAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepo.deleteUser(user.email)
        }
    }
}

// ── States ─────────────────────────────────────────────────

sealed class BiometricState {
    object Idle : BiometricState()
    object Authenticating : BiometricState()
    object Success : BiometricState()
}

sealed class BiometricCheckResult {
    object Available : BiometricCheckResult()
    object NotEnrolled : BiometricCheckResult()    // supported but no finger added
    object NotSupported : BiometricCheckResult()   // no hardware
}


//    private fun fetchUsers() {
//        viewModelScope.launch {
//            userRepository.allUsers
//                .flowOn(Dispatchers.IO)
//                .catch { e ->
//                    // handle exception
//                }
//                .collect {  user ->
//                _users.value =user
//                }
//        }
//    }




