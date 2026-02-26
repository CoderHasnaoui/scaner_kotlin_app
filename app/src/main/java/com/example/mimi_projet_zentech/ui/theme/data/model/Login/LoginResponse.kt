package com.example.mimi_projet_zentech.ui.theme.data.model.Login

import com.google.gson.annotations.SerializedName

class LoginResponse(
    @SerializedName("access_token")
    val token: String?,
    @SerializedName("token_type")
    val tokenType: String?,

    @SerializedName("message")
    val message: String?
)