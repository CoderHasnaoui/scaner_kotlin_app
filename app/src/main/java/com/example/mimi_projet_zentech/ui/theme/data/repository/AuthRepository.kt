package com.example.mimi_projet_zentech.ui.theme.data.repository

import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.ui.theme.data.model.Login.AuthApi
import com.example.mimi_projet_zentech.ui.theme.data.model.Login.LoginRequest
import com.example.mimi_projet_zentech.ui.theme.data.model.Login.LoginResponse

import retrofit2.Response

class AuthRepository(private  val api: AuthApi) {

    suspend fun login(request : LoginRequest): Response< LoginResponse> {
        return api.login(request)
    }
    suspend fun fetchMerchnat(): Response<List<MerchantGroup>>{
        return api.getMerchants()
    }
    suspend fun getMerchantBySlug(slug: String): MerchantGroup? {
        val response = fetchMerchnat()
        return if (response.isSuccessful) {
            response.body()?.find { it.slug == slug }
        } else {
            null
        }
    }
}