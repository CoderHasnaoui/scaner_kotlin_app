package com.example.mimi_projet_zentech.ui.theme.ui.statusScreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.SessionManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.model.Ticket.TicketInfos
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.TicketRepository
import com.example.mimi_projet_zentech.ui.theme.ThemeRepository

import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.ui.scanScreen.ScanUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch
import kotlin.getValue

class ValidViewModel (application: Application): AndroidViewModel(application) {
    val   themeRepo = ThemeRepository(application)
    var isDark =  themeRepo.isDarkMode()
        private set
    private val tokenManager = TokenManager(application)
    private val api by lazy {
        RetrofitInstance.getPrivateApi(tokenManager) { SessionManager.notifyTokenExpired() }
    }
    private val ticketRepository by lazy { TicketRepository(api) }

    // Use a StateFlow for the UI
    private val _stateUi = MutableStateFlow<ValidUiState>(ValidUiState.Loading)
    val uiState: StateFlow<ValidUiState> = _stateUi.asStateFlow()
    fun loadTicket(ticketNum:String? , scanStatus : ScanStatus) {
        if (scanStatus == ScanStatus.NOT_FOUND || ticketNum.isNullOrEmpty()) {
            _stateUi.value = ValidUiState.Success(null)

            return
        }
        viewModelScope.launch {
            try {
//                val response = api.checkTicket(ticketNum)
                val response = ticketRepository.checkTicket(ticketNum)
                if (response.isSuccessful) {

                    _stateUi.value = ValidUiState.Success(response.body())
                }else{
                    _stateUi.value = ValidUiState.Error("Not Found")
                }
            } catch (e: Exception) { _stateUi.value = ValidUiState.Error(e.message ?: "Error")
//                isLoading=false
            }
        }


    }


}