package com.example.mimi_projet_zentech.ui.theme.ui.scanScreen

sealed interface ScanUiState {
    object Initializing : ScanUiState

    object  Ready : ScanUiState
    data class Verifiying(val ticketNumber: String) : ScanUiState // proceccing

    data class Error(val message: String) : ScanUiState
}