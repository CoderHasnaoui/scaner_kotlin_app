package com.example.mimi_projet_zentech.ui.theme.ui.multipleAccount

import com.example.mimi_projet_zentech.data.local.entity.userAccount.UserAccount

sealed class AccountUiState {
    object Loading : AccountUiState()
    object NoAccount : AccountUiState()
    data class SingleAccount(val user: UserAccount) : AccountUiState()
    data class MultipleAccounts(val users: List<UserAccount>) : AccountUiState()
}