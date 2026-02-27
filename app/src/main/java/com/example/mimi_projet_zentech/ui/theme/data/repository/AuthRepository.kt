package com.example.mimi_projet_zentech.ui.theme.data.repository

import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.ui.theme.data.remote.AuthApi
import com.example.mimi_projet_zentech.ui.theme.data.model.Login.LoginRequest
import com.example.mimi_projet_zentech.ui.theme.data.model.Login.LoginResponse

import retrofit2.Response

class AuthRepository(private  val api: AuthApi) {

    suspend fun login(request : LoginRequest): Response< LoginResponse> {
        return api.login(request)
    }

}