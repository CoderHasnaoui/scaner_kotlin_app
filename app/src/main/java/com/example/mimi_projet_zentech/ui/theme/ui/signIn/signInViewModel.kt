package com.example.mimi_projet_zentech.ui.theme.ui.signIn

import android.app.Application
import android.content.Context

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.local.db.DatabaseProvider
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount
import com.example.mimi_projet_zentech.data.model.Login.LoginRequest
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.AuthRepository
import com.example.mimi_projet_zentech.data.repository.MerchantRepository
import com.example.mimi_projet_zentech.data.repository.UserAccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.biometric.BiometricPrompt


class  SignInViewModel (application: Application): AndroidViewModel(application) {
    // db Local CAll
    val  db = DatabaseProvider.getDatabase(application)
    private val RoomRepo by lazy { UserAccountRepository(db.userAccountDao()) }
    //Api Calles
    private val tokenManager = TokenManager(getApplication())
    private val repository = AuthRepository(RetrofitInstance.publicApi)
    private val userRepository = UserRepository(getApplication<Application>().dataStore)
    var onLoginSuccess: (()->Unit)?  = null
    // SignState
    private val _state = MutableStateFlow<SignInState>(SignInState.Ready)
    val state :  StateFlow<SignInState> = _state.asStateFlow()
    private val _fields = MutableStateFlow(SignInData())
    val fields: StateFlow<SignInData> = _fields.asStateFlow()



    fun onEmailChanged(newValue: String) {
    _fields.update { it.copy(email = newValue , emailError = null) }
    }
    fun onPasswordChanged(newValue: String) {
        _fields.update { it.copy(password = newValue.trim(), passwordError = null) }
    }
    fun onSignInClicked() {
        val curentState = _fields.value

        val emailErr = when {
            curentState.email.isEmpty() -> "Field is required"
            "@" !in curentState.email -> "Email must contain @"
            else -> null
        }

        val passwordErr = when {
            curentState.password.isEmpty() -> "Field is Required"
            curentState.password.length<8 ->  "Password must be at least 5 characters"
            else -> null
        }

        if(emailErr!=null || passwordErr!=null){
            _fields.update { it.copy(emailError = emailErr, passwordError = passwordErr) }
            return

        }
//        if (!isNetworkAvailable()) {
//            _state.update { it.copy(loginMessage = "No internet connection.") }
//            return
//        }
        if (!isNetworkAvailable()) {
            _state.value = SignInState.Error("No internet connection.")
            return
        }

            viewModelScope.launch {
                _state.value = SignInState.Loading
                try {
                    val request = LoginRequest(
                        email = curentState.email,
                        password = curentState.password,
                        gToken = "",

                        platform = "android",
                        rememberMe = true
                    )
                    val response = repository.login(request)
                    if (response.isSuccessful) {
                        val loginStartTime = System.currentTimeMillis()
                        val data = response.body()
                        val token = data?.token
                        val name = data?.user?.name?:"unknow "
                        val emailApi  = data?.user?.email?:"exemple@gmail.com"
                        if (token != null) {
                            tokenManager.saveToken(token)
                            tokenManager.setLoggedIn(true)
                            userRepository.saveUserInfo(name , emailApi)

                            // save data in storeage
                            withContext(Dispatchers.IO){
                                val user = RoomRepo.getUserByEmail(emailApi)
                                if(user==null){
                                    RoomRepo.saveUser(UserAccount(email = emailApi ,  name  , initilas =name.take(1) ,  loginStartTime ))

                                }else{
                                    RoomRepo.updateLastSession(emailApi , loginStartTime)
                                }
                            }
                            // ← NEW: check if first time → ask biometric
                            val isFirstTime = RoomRepo.getUserByEmail(emailApi)?.let { true } ?: false
                            val biometricEnabled = userRepository.isBiometricEnabled().first()
                            val biometricAsked = userRepository.isBiometricAsked().first()
                            if (!biometricEnabled) {
                                _state.value = SignInState.ShowBiometricDialog  // ← ask user
                            } else {
                                onLoginSuccess?.invoke()
                            }
                            //cal lamda to go Login
//                            onLoginSuccess?.invoke()

                        } else {
                            _state.value = SignInState.Error("Invalid credentials")
                        }

                    } else {

                        val errorText = response.errorBody()?.string() ?:""

                        val message = if( "did not match our records" in errorText  ){
                            _state.value = SignInState.Error("Invalid credentials")
                        } else {
                            _state.value = SignInState.Error("Login failed. Please try again.")
                        }

                    }
                } catch (e: Exception) {
                    _state.value = SignInState.Error("Something Wrong.")
                }
//                finally {
//                    _uiState.update { it.copy(isLoading = false) }
//                }
            }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    // dialog result — launch biometric to ENCRYPT password
    fun onBiometricDialogResult(accepted: Boolean, activity: FragmentActivity) {
        if (!accepted) {
            // ← user clicked "Not now" → just go home
            viewModelScope.launch {
                userRepository.setBiometricAsked(true)
                onLoginSuccess?.invoke()
                _state.value = SignInState.Ready
            }
            return
        }

        // ← user clicked "Enable" → show fingerprint prompt
        val cipher = tokenManager.getEncryptCipher()

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult  // ← result is HERE
                ) {
                    val authenticatedCipher = result.cryptoObject?.cipher!!
                    val (encryptedPassword, iv) = tokenManager.encryptPassword(
                        authenticatedCipher,
                        _fields.value.password
                    )
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            RoomRepo.updateEncryptedPassword(
                                email = _fields.value.email,
                                encryptedPassword = encryptedPassword,
                                passwordIv = iv
                            )
                        }
                        userRepository.setBiometricAsked(true)
                        userRepository.setBiometricEnabled(true)
                        onLoginSuccess?.invoke()
                        _state.value = SignInState.Ready
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    // ← cancelled fingerprint → just go home, biometric stays false
                    viewModelScope.launch {
                        userRepository.setBiometricAsked(true)
                        onLoginSuccess?.invoke()
                        _state.value = SignInState.Ready
                    }
                }

                override fun onAuthenticationFailed() {}
            }
        )

        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Enable Fingerprint Login")
                .setSubtitle("Verify your fingerprint to enable quick login")
                .setNegativeButtonText("Cancel")
                .build(),
            BiometricPrompt.CryptoObject(cipher)
        )
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
