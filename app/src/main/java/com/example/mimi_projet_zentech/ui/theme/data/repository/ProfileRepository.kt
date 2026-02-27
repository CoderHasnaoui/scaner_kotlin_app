package com.example.mimi_projet_zentech.ui.theme.data.repository
import com.example.mimi_projet_zentech.ui.theme.data.remote.AuthApi

import com.example.mimi_projet_zentech.ui.theme.data.model.UserProfile.UserProfileResponse
import retrofit2.Response

class ProfileRepository (private val api : AuthApi ) {
    suspend fun getPrifile() : Response<UserProfileResponse>
    {
        return api.getProfile()
    }
}