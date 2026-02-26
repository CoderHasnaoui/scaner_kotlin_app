package com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant

import retrofit2.Response
import retrofit2.http.GET

interface MerchantApi {
    @GET("users/group-merchants")
    suspend fun getMerchants(): Response<List<MerchantGroup>>
}