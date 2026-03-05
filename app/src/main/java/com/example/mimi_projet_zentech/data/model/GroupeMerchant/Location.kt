package com.example.mimi_projet_zentech.data.model.GroupeMerchant

import com.google.gson.annotations.SerializedName

data class Location (
    @SerializedName("name")
    val name: String,
    val token: String
    )