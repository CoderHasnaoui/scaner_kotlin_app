package com.example.mimi_projet_zentech.data.repository

import com.example.mimi_projet_zentech.data.model.UserProfile.UserProfileResponse
import com.example.mimi_projet_zentech.data.remote.AuthApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class ProfileRepository @Inject constructor (@Named("private") private val api : AuthApi) {
    suspend fun getPrifile() : Response<UserProfileResponse>
    {
        return api.getProfile()
    }
}