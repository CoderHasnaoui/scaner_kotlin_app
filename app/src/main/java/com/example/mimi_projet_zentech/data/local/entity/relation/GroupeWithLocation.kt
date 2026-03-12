package com.example.mimi_projet_zentech.data.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mimi_projet_zentech.data.local.entity.LocationEntity
import com.example.mimi_projet_zentech.data.local.entity.MerchantEntity

    data class GroupeWithLocation(
        @Embedded val merchantGroup: MerchantEntity,
        @Relation(
            parentColumn = "slug",
            entityColumn = "merchantGroupSlug"
        )
        val location: List<LocationEntity>

    )
