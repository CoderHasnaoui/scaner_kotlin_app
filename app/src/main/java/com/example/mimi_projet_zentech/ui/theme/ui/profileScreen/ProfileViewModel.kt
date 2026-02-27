package com.example.mimi_projet_zentech.ui.theme.ui.profileScreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.ui.theme.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.ui.theme.data.repository.AuthRepository
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.UserRepository
import com.example.mimi_projet_zentech.ui.theme.ui.signIn.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
     val tokenManager = TokenManager(application)
    private val userRepository = UserRepository(getApplication<Application>().dataStore)

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
        // CACHE: If data is already loaded, don't hit the API again
        if (selectedMerchant != null )return


        viewModelScope.launch {
            isLoading = true
            try {
                // Get the private API using the token
                val api = RetrofitInstance.getPrivateApi(tokenManager)
                // 1. Fetch User Info (New)
//

                val slug = tokenManager.getSlug()
                if (slug != null) {
                    val repository = AuthRepository(api)
                    selectedMerchant = repository.getMerchantBySlug(slug)
                }


                // Fetch specific merchant from the API list

            } catch (e: Exception) {
//                userName = "Error loading"

            } finally {
                isLoading = false
            }
        }
    }
}