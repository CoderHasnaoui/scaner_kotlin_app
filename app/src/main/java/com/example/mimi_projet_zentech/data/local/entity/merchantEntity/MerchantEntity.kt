package com.example.mimi_projet_zentech.data.local.entity.merchantEntity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Merchant")
data class MerchantEntity(
    val name: String,
    @PrimaryKey
    val slug: String,


)