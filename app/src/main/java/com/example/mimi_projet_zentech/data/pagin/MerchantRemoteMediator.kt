package com.example.mimi_projet_zentech.data.pagin

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.mimi_projet_zentech.data.local.dao.MerchantDao
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.MerchantEntity
import com.example.mimi_projet_zentech.data.model.GroupeMerchant.MerchantApi
@OptIn(ExperimentalPagingApi::class)

class MerchantRemoteMediator(
    private val api : MerchantApi ,
    private val dao : MerchantDao
) : RemoteMediator<Int, MerchantEntity>(){
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MerchantEntity>
    ): MediatorResult {
        return try{
            // witch page to load
            val page =  when (loadType){
                LoadType.REFRESH -> {1}

                LoadType.PREPEND -> {return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    // calcul next page
                    val totalLoaded = state.pages.sumOf { it.data.size }
                    val nextPage = totalLoaded/state.config.pageSize   + 1

                    nextPage
                }
            }
            // call api
            val response = api.getMerchants()



        }catch (e:Exception){
            MediatorResult.Error(e) // something Wrong
        } as MediatorResult
    }
}