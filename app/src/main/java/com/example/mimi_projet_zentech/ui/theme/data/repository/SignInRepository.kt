package com.example.mimi_projet_zentech.ui.theme.data.repository

import com.example.mimi_projet_zentech.ui.theme.data.model.User
import com.example.mimi_projet_zentech.ui.theme.util.LoginResult
import kotlinx.coroutines.delay

class SignInRepository {
    private val registeredUsers = arrayListOf(
        User("admin@zentech.com", "123456"),
        User("user@test.com", "password"),
        User("abdilah@kotlim.ma", "2026")
    )
    suspend fun login(email: String, pass: String): LoginResult {
        delay(1500)
        val user = registeredUsers.find { it.email == email && it.pass == pass }
        return if (user!=null) {
            LoginResult.Success("Welcome back, Zentech User!")

        } else {
            LoginResult.Error("Invalid email or password")
        }
    }
}