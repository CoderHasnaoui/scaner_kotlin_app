package com.example.mimi_projet_zentech.ui.theme.ui.homePage

import com.example.mimi_projet_zentech.data.model.GroupeMerchant.MerchantGroup

sealed interface  HomeUiState{
    object Loading : HomeUiState
    object Empty : HomeUiState
    object Redirecting : HomeUiState
    object NoResults  : HomeUiState
    data class Success(val merchants :List<MerchantGroup>)  : HomeUiState
    data class Error(val message :String ) : HomeUiState
}
