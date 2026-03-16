package com.example.mimi_projet_zentech.ui.theme.ui.profileScreen

import android.app.Application
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.SessionManager
import com.example.mimi_projet_zentech.data.local.SlugManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.local.db.DatabaseProvider
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount
import com.example.mimi_projet_zentech.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.data.model.Login.LoginRequest
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.AuthRepository
import com.example.mimi_projet_zentech.data.repository.MerchantRepository
import com.example.mimi_projet_zentech.data.repository.UserAccountRepository
import com.example.mimi_projet_zentech.ui.theme.ThemeRepository

import com.example.mimi_projet_zentech.ui.theme.ui.signIn.UserRepository
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.crypto.Cipher

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val tokenManager = TokenManager(application)
    private val RoomRepo by lazy { UserAccountRepository(db.userAccountDao()) }

    val db = DatabaseProvider.getDatabase(application)
    val api = RetrofitInstance.getPrivateApi(tokenManager , onTokenExpired = { SessionManager.notifyTokenExpired()})

    val repository = MerchantRepository(api ,db.merchantDao() )
    val themeRepo = ThemeRepository(context = application)

    val slugManager = SlugManager(context = application)
    private val userRepository = UserRepository(getApplication<Application>().dataStore)
    val isBiometricEnabled: StateFlow<Boolean> = userRepository
        .isBiometricEnabled()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    private val repoSign = AuthRepository(RetrofitInstance.publicApi)
    var isReady by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    // UI States
    var selectedMerchant by mutableStateOf<MerchantGroup?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

// i use it for my check if taht user have password endcrypted in Room
    val currentUser: StateFlow<UserAccount?> = userRepository.email
        .flatMapLatest { email ->
            RoomRepo.getUserByEmailFlow(email)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val userName: StateFlow<String> = userRepository.name
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Loading..."
        )

    val userEmail: StateFlow<String> = userRepository.email
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Loading..."
        )

    val userInitial: String
        get() {
            if (userName.value == "Loading..." || userName.value.isBlank()) return "--"
            val parts = userName.value.trim().split(" ")
            return if (parts.size >= 2) {
                "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}".uppercase()
            } else {
                "${userName.value.firstOrNull() ?: ""}".uppercase()
            }
        }



//    init {
//        userEmail = tokenManager.getEmail() ?: "User@email.com"
//    }

    fun loadProfileData() {
        // don't get data again if it was
        if (selectedMerchant != null )return


        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {


                val slug = slugManager.getSlug()
                if (slug != null) {

                    val result: MerchantGroup? = repository.getMerchantBySlug(slug)

                    if (result != null) {
                        selectedMerchant = result
                    } else {
                        errorMessage = "Merchant not found."
                    }

                }else{
                    errorMessage = "No Business Groupe Selected "
                }

            } catch (e: Exception) {
                errorMessage = "Failed to load profile. Please check your connection."
                e.printStackTrace()
            } finally {
                isLoading = false
                isReady = true

            }
        }
    }
    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userRepository.setBiometricEnabled(enabled)
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    fun verifyPasswordAndGetCipher(
        password: String,
        email: String,
        onSuccess: (Cipher, String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("BIOMETRIC", "verifyPassword called with email = $email")

                val request = LoginRequest(
                    email = email,
                    password = password,
                    gToken = "",
                    platform = "android",
                    rememberMe = true
                )
                val response = repoSign.login(request)
                Log.d("BIOMETRIC", "response code = ${response.code()}")

                if (!response.isSuccessful) {
                    Log.d("BIOMETRIC", "wrong password")
                    onError("Wrong password")
                    return@launch
                }

                Log.d("BIOMETRIC", "password correct → getting cipher")
                val cipher = tokenManager.getEncryptCipher()
                Log.d("BIOMETRIC", "cipher ready → calling onSuccess")
                onSuccess(cipher, password)
            } catch (e: Exception) {
                Log.d("BIOMETRIC", "exception = ${e.message}")
                onError("Something went wrong")
            }
        }
    }
    fun saveEncryptedPassword(
        encryptedPassword: String,
        iv: String,
        email: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                RoomRepo.updateEncryptedPassword(
                    email = email,
                    encryptedPassword = encryptedPassword,
                    passwordIv = iv
                )
            }
            setBiometricEnabled(true)
            Log.d("BIOMETRIC", "saved in Room")
        }
    }

    fun onBiometricSwitchEnabled(
        onNeedPassword: () -> Unit,
//        onDirectEnable: () -> Unit
    ) {

        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) {
                RoomRepo.getUserByEmail(userEmail.value)
            }
            Log.d("BIOMETRIC", "email = ${userEmail.value}")
            Log.d("BIOMETRIC", "encryptedPassword = ${user?.encryptedPassword}")
            Log.d("BIOMETRIC", "passwordIv = ${user?.passwordIv}")
            if (user?.encryptedPassword == null) {

                Log.d("BIOMETRIC", " show password dialog")
                onNeedPassword()
            } else {
                Log.d("BIOMETRIC", "direct enable")
                setBiometricEnabled(true)
//                onDirectEnable()
            }
        }
    }
}