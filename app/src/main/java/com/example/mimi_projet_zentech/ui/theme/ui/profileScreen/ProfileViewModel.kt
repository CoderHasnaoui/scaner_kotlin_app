package com.example.mimi_projet_zentech.ui.theme.ui.profileScreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.SessionManager
import com.example.mimi_projet_zentech.data.local.SlugManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.MerchantRepository
import com.example.mimi_projet_zentech.ui.theme.ThemeRepository

import com.example.mimi_projet_zentech.ui.theme.ui.signIn.UserRepository
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val themeRepo = ThemeRepository(context = application)
     val tokenManager = TokenManager(application)
    val slugManager = SlugManager(context = application)
    private val userRepository = UserRepository(getApplication<Application>().dataStore)

    var isReady by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    // UI States
    var selectedMerchant by mutableStateOf<MerchantGroup?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

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

                val api = RetrofitInstance.getPrivateApi(tokenManager , onTokenExpired = { SessionManager.notifyTokenExpired()})

                val slug = slugManager.getSlug()
                if (slug != null) {
                    val repository = MerchantRepository(api)
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
}