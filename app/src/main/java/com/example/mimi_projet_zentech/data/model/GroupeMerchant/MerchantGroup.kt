package com.example.mimi_projet_zentech.data.model.GroupeMerchant

data class MerchantGroup(

    val name: String,
    val slug: String,
    val locations: List<Location>
)