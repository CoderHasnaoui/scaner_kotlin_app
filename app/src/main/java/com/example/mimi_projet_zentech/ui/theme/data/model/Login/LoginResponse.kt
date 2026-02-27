package com.example.mimi_projet_zentech.ui.theme.data.model.Login

import com.example.mimi_projet_zentech.ui.theme.data.model.UserProfile.UserProfileResponse
import com.google.gson.annotations.SerializedName

class LoginResponse(
    @SerializedName("access_token")
    val token: String?,
    @SerializedName("user")
    val user: UserProfileResponse?,

//    @SerializedName("message")
//    val message: String?
)