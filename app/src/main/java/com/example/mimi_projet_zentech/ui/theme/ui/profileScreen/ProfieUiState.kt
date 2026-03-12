package com.example.mimi_projet_zentech.ui.theme.ui.profileScreen

import com.example.mimi_projet_zentech.data.model.GroupeMerchant.MerchantGroup

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val merchant: MerchantGroup ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}