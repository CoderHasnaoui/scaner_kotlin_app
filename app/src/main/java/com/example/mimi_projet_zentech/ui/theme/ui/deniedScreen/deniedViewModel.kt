package com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.SessionManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.TicketRepository

import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus

import kotlinx.coroutines.launch

class DeniedViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val api = RetrofitInstance.getPrivateApi(
        tokenManager,
        onTokenExpired = { SessionManager.notifyTokenExpired() }
    )
    private val ticketRepository = TicketRepository(api)

    var isProcessing by mutableStateOf(false)
        private set

    var onNavigate: ((ticketNum: String, scanStatus: ScanStatus) -> Unit)? = null

    fun handleManualEntry(passId: String) {
        val trimmed = passId.trim()
        if (trimmed.isEmpty() || isProcessing) return

        isProcessing = true
        viewModelScope.launch {
            val isLink = trimmed.startsWith("http") || trimmed.startsWith("www")

            val scanStatus: ScanStatus = if (isLink) {
                ScanStatus.NOT_FOUND
            } else {
                try {
                    val response = ticketRepository.checkTicket(trimmed)
                    if (response.isSuccessful) {
                        val ticket = response.body()
                        if (ticket != null) {
                            if (ticket.isScanned) {
                                ScanStatus.ALREADY_SCANNED
                            } else {
                                ScanStatus.VALID
                            }
                        } else ScanStatus.NOT_FOUND
                    } else ScanStatus.NOT_FOUND
                } catch (e: Exception) {
                    ScanStatus.NOT_FOUND
                }
            }

            val safeResult = Uri.encode(trimmed)
            onNavigate?.invoke(safeResult, scanStatus)
            isProcessing = false
        }
    }
}