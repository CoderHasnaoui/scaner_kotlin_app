package com.example.mimi_projet_zentech.data.repository


import com.example.mimi_projet_zentech.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.data.remote.AuthApi
import retrofit2.Response


class MerchantRepository(private val api: AuthApi) {

    suspend fun getMerchants(): Response<List<MerchantGroup>> {
        return api.getMerchants()
    }

    suspend fun getMerchantBySlug(slug: String): MerchantGroup? {
        val response = getMerchants()
        return if (response.isSuccessful) {
            response.body()?.find { it.slug == slug }
        } else {
            null
        }
    }

}