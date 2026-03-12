package com.example.mimi_projet_zentech.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mimi_projet_zentech.data.local.dao.MerchantDao
import com.example.mimi_projet_zentech.data.local.dao.UserAccountDao
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.LocationEntity
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.MerchantEntity
import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount

@Database(
    entities = [
        MerchantEntity::class,
        LocationEntity::class,
        UserAccount::class
    ] ,
    version =  1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun merchantDao(): MerchantDao

    abstract fun userAccountDao(): UserAccountDao
}

