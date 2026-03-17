package com.example.mimi_projet_zentech.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.LocationEntity
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.MerchantEntity
import com.example.mimi_projet_zentech.data.local.entity.relation.GroupeWithLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend  fun insertMerchantGroup(group: MerchantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend  fun insertLocation(location: List<LocationEntity>)

    @Transaction
    @Query("SELECT * FROM Merchant ")
      fun getMErchantGroupe(): Flow<List<GroupeWithLocation>>

    @Transaction
    @Query("SELECT * FROM Merchant")
    suspend fun getStaticMerchantGroups(): List<GroupeWithLocation>
    @Transaction
    @Query("SELECT * FROM MERCHANT")
    fun getMerchantPaged(): PagingSource<Int , GroupeWithLocation>
}




