package com.example.mimi_projet_zentech.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mimi_projet_zentech.data.local.dao.MerchantDao
import com.example.mimi_projet_zentech.data.local.entity.LocationEntity
import com.example.mimi_projet_zentech.data.local.entity.MerchantEntity

@Database(
    entities = [
        MerchantEntity::class,
        LocationEntity::class
    ] ,
    version =  1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun merchantDao(): MerchantDao
}

