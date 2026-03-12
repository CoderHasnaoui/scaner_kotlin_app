package com.example.mimi_projet_zentech.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationEntity")
data class LocationEntity(
    @PrimaryKey
    val id :Int  =   0  ,
    val name: String,
    val merchantGroupSlug: String // fK

)
