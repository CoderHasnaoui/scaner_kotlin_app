package com.example.mimi_projet_zentech.data.repository

import com.example.mimi_projet_zentech.data.model.Login.LoginRequest
import com.example.mimi_projet_zentech.data.model.Login.LoginResponse
import com.example.mimi_projet_zentech.data.remote.AuthApi


import retrofit2.Response

class AuthRepository(private  val api: AuthApi) {

    suspend fun login(request : LoginRequest): Response<LoginResponse> {
        return api.login(request)
    }

}