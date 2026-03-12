package com.example.mimi_projet_zentech.ui.theme.ui.statusScreen

import com.example.mimi_projet_zentech.data.model.Ticket.TicketInfos

sealed interface ValidUiState {
    object Initializing : ValidUiState
    object Loading : ValidUiState
    data class Success(val ticket: TicketInfos?) : ValidUiState
    data class Error(val message: String) : ValidUiState
}