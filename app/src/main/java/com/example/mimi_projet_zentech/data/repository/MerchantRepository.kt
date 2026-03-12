package com.example.mimi_projet_zentech.data.repository


import com.example.mimi_projet_zentech.data.local.dao.MerchantDao
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.LocationEntity
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.MerchantEntity
import com.example.mimi_projet_zentech.data.local.entity.relation.GroupeWithLocation
import com.example.mimi_projet_zentech.data.model.GroupeMerchant.Location
import com.example.mimi_projet_zentech.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.data.remote.AuthApi
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


class MerchantRepository(private val api: AuthApi , private val dao: MerchantDao) {

    // Read From Room
    val allMerchantFlow : Flow<List<GroupeWithLocation>> = dao.getMErchantGroupe()

    // fetch Api update Db
    suspend fun getMerchants(): Response<List<MerchantGroup>> {
        return api.getMerchants()
    }
    suspend fun refrechMerchant() {
        try{


        val response = api.getMerchants()
        if (response.isSuccessful) {
            val group = response.body() ?: emptyList()
            val Mentities = group.map { group ->
                MerchantEntity(
                    name = group.name,
                    slug = group.slug,

                )
            }
            val locatioonEntity = group.flatMap { group ->
                group.locations.map { location ->
                    LocationEntity(
                        name = location.name,
                        merchantGroupSlug = group.slug
                    )
                }
            }
            saveToDb(merchants = Mentities  , locations = locatioonEntity)
        }
        } catch (e: java.net.UnknownHostException) {

            println("keep old data cannot reach to network.")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
    suspend fun getMerchantBySlug(slug: String): MerchantGroup? {
        val response = getMerchants()
        return if (response.isSuccessful) {
            response.body()?.find { it.slug == slug }
        } else {
            null
        }
    }
    private suspend fun saveToDb(merchants: List<MerchantEntity>, locations: List<LocationEntity>) {
        merchants.forEach { dao.insertMerchantGroup(it) }
        dao.insertLocation(locations)
    }
    suspend fun getCurrentMerchantsOnce(): List<GroupeWithLocation> {
        return dao.getStaticMerchantGroups()
    }

// change from ! MerchantGroupe to MErchnatwithGroupes

fun GroupeWithLocation.toMerchantGroup(): MerchantGroup {
    return MerchantGroup(
        name = this.merchantGroup.name,
        slug = this.merchantGroup.slug,
        locations = this.location.map { it.toLocation() }
    )
}
    fun LocationEntity.toLocation(): Location {
        return Location(
            name = this.name
        )
    }



}