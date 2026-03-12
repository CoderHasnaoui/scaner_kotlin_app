package com.example.mimi_projet_zentech.data.repository

import com.example.mimi_projet_zentech.data.local.dao.UserAccountDao
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount
import kotlinx.coroutines.flow.Flow

class UserAccount (private val dao: UserAccountDao) {
    val allUsers: Flow<List<UserAccount>>? = dao.getUsers()
    suspend fun getUserByEmail(email: String): UserAccount? {
        return dao.getUserByemaill(email)
    }
    suspend fun saveUser(userAccount: UserAccount) {
        dao.saveUser(userAccount)
    }
    suspend fun deleteUser(email: String) {
        dao.deleteUser(email)
    }

}