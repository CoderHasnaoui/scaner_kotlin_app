package com.example.mimi_projet_zentech.ui.theme.ui.scanScreen

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.mimi_projet_zentech.ui.theme.data.local.SessionManager
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.ui.theme.data.repository.LocationRepository
import com.example.mimi_projet_zentech.ui.theme.data.repository.TicketRepository


class ScanViewMode(application: Application): AndroidViewModel(application) {
    private val tokenmanager = TokenManager(getApplication())
    private val api = RetrofitInstance.getPrivateApi(
        tokenmanager ,
        onTokenExpired = { SessionManager.notifyTokenExpired()}
    )
    private  val ticketRepository  = TicketRepository(api)

    private val locationRepository = LocationRepository(api)

    var isProcessing by  mutableStateOf(false)
        private set
    var isFlashOn by mutableStateOf(false)
        private set


}