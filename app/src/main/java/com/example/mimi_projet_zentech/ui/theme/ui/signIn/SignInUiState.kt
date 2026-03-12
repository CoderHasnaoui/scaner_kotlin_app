package com.example.mimi_projet_zentech.ui.theme.ui.signIn

data class SignInData(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
)

// The overall status of the screen
sealed interface SignInState {
    object Ready  : SignInState // default
    object Loading : SignInState
    object ShowBiometricDialog : SignInState
    data class Success(val userName: String) : SignInState
    data class Error(val message: String) : SignInState

}