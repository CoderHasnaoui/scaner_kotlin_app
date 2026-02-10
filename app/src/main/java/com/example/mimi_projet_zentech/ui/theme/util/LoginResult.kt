package com.example.mimi_projet_zentech.ui.theme.util

sealed class LoginResult {
    data class Success(val message: String) : LoginResult()
    data class Error(val errorMessage: String) : LoginResult()
    object Loading : LoginResult()
}