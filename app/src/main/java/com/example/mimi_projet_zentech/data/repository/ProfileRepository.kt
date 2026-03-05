package com.example.mimi_projet_zentech.data.repository

import com.example.mimi_projet_zentech.data.model.UserProfile.UserProfileResponse
import com.example.mimi_projet_zentech.data.remote.AuthApi
import retrofit2.Response

class ProfileRepository (private val api : AuthApi) {
    suspend fun getPrifile() : Response<UserProfileResponse>
    {
        return api.getProfile()
    }
}