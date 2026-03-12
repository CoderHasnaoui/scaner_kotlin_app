package com.example.mimi_projet_zentech.data.local.entity.userAccount

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user_account")
data class UserAccount(
        @PrimaryKey
            val email: String,
            val name :String ,
            val initilas : String ,
            val lastLoginTime: Long = 0L

        )