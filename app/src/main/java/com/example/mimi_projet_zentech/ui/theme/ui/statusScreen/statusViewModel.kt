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

import kotlinx.coroutines.launch
import kotlin.getValue

class ValidViewModel (application: Application): AndroidViewModel(application) {
    val   themeRepo = ThemeRepository(application)
    var isDark =  themeRepo.isDarkMode()
        private set
    private val tokenManager  = TokenManager(getApplication())
    private val api  by lazy {
        RetrofitInstance.getPrivateApi(
        tokenManager ,
        onTokenExpired = { SessionManager.notifyTokenExpired()}

    ) }
    private val ticketRepository by lazy { TicketRepository(api) }
    var ticket by mutableStateOf<TicketInfos?>(null)
        private set
    var isLoading by mutableStateOf(true)
        private  set
    var isReady by mutableStateOf(false)
    private set
    fun loadTicket(ticketNum:String? , scanStatus : ScanStatus) {
        if (scanStatus == ScanStatus.NOT_FOUND || ticketNum.isNullOrEmpty()) {
            isReady = true
            isLoading = false
            return
        }
        viewModelScope.launch {
            try {
//                val response = api.checkTicket(ticketNum)
                val response = ticketRepository.checkTicket(ticketNum)
                if (response.isSuccessful) {
                    ticket = response.body()
                }
            } catch (e: Exception) { }
            finally {
                isReady = true
                isLoading=false
            }
        }


    }


}