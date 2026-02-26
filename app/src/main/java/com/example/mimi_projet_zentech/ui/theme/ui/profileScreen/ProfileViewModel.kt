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
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
     val tokenManager = TokenManager(application)

    // UI States
    var selectedMerchant by mutableStateOf<MerchantGroup?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    var userName by mutableStateOf("Loading...")
    var userEmail by mutableStateOf("Loading...")
        val userInitial:String
        get() {
            if (userName == "Loading..." || userName.isBlank()) return "--"

            val parts = userName.trim().split(" ")
            return if (parts.size >= 2) {
                // Take 1 letter and second lettre
                "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}".uppercase()
            } else {
                // Just one Letter
                "${userName.firstOrNull() ?: ""}".uppercase()
            }
        }

//    init {
//        userEmail = tokenManager.getEmail() ?: "User@email.com"
//    }

    fun loadProfileData() {
        // CACHE: If data is already loaded, don't hit the API again
        if (selectedMerchant != null && userEmail != "Loading...") return


        viewModelScope.launch {
            isLoading = true
            try {
                // Get the private API using the token
                val api = RetrofitInstance.getPrivateApi(tokenManager)
                // 1. Fetch User Info (New)
                val userResponse = api.getProfile()
                if (userResponse.isSuccessful) {
                    val user = userResponse.body()
                    userName = user?.name ?: ""
                    userEmail = user?.email ?: ""
                }

                val slug = tokenManager.getSlug()
                if (slug != null) {
                    val repository = AuthRepository(api)
                    selectedMerchant = repository.getMerchantBySlug(slug)
                }


                // Fetch specific merchant from the API list

            } catch (e: Exception) {
                userName = "Error loading"
            } finally {
                isLoading = false
            }
        }
    }
}