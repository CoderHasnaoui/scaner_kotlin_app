package com.example.mimi_projet_zentech.ui.theme.data.repository

import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.MerchantApi
import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.MerchantGroup
import retrofit2.Response


class MerchantRepository(private val api : MerchantApi) {
    suspend fun fetchMerchnat(): Response<List<MerchantGroup>>{
        return api.getMerchants()
    }
}