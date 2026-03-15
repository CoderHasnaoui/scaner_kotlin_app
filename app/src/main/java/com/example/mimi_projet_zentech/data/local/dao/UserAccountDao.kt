package com.example.mimi_projet_zentech.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(userAccount: UserAccount)
    @Query("SELECT * FROM user_account ORDER BY lastLoginTime DESC ")
     fun getUsers(): Flow<List<UserAccount>>
    @Query("SELECT * FROM user_account WHERE email = :email")
    suspend fun getUserByemaill(email: String): UserAccount?
    @Query("DELETE FROM user_account where email=:email")
    suspend fun deleteUser(email: String)
    @Query("UPDATE user_account SET lastLoginTime = :sessionTime WHERE email = :email")
    suspend fun updateLastSession(email: String, sessionTime: Long)
    @Query("UPDATE user_account SET encryptedPassword = :encryptedPassword, passwordIv = :passwordIv WHERE email = :email")
    suspend fun updateEncryptedPassword(email: String, encryptedPassword: String, passwordIv: String)
    @Query("SELECT * FROM user_account WHERE email = :email")
    fun getUserByEmailFlow(email: String): Flow<UserAccount?>
}




