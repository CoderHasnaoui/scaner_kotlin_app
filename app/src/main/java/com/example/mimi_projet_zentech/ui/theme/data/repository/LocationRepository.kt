package com.example.mimi_projet_zentech.ui.theme.data.repository

import com.example.mimi_projet_zentech.ui.theme.data.remote.AuthApi
import retrofit2.Response

class LocationRepository (private val api : AuthApi){

    suspend fun getLocations(slug: String): Response<Any> {
        return api.getLocations(slug)
    }
}