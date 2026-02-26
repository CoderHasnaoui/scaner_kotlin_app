package com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant

import com.google.gson.annotations.SerializedName

data class Location (
    @SerializedName("name")
    val name: String,
    val token: String
    )