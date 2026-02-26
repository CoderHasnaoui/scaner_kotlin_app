package com.example.mimi_projet_zentech.ui.theme.data.model.Login

data class LoginRequest (
    val email :String ,
    val password :String ,
    val gToken :String  ,
    val platform :String  ,
    val rememberMe : Boolean

)