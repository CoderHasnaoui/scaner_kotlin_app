package com.example.mimi_projet_zentech.data.repository

import com.example.mimi_projet_zentech.data.remote.AuthApi
import retrofit2.Response

class LocationRepository (private val api : AuthApi){

    suspend fun getLocations(slug: String): Response<Any> {
        return api.getLocations(slug)
    }
}