package com.example.mimi_projet_zentech.data.local.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.LocationEntity
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.MerchantEntity

    data class GroupeWithLocation(
        @Embedded val merchantGroup: MerchantEntity,
        @Relation(
            parentColumn = "slug",
            entityColumn = "merchantGroupSlug"
        )
        val location: List<LocationEntity>

    )
