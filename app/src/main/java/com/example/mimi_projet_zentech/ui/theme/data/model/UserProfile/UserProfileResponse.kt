package com.example.mimi_projet_zentech.ui.theme.data.model.UserProfile

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializer

class UserProfileResponse (
    @SerializedName("name")
    val name :String ,
    @SerializedName("email")
    val email :String ,

)