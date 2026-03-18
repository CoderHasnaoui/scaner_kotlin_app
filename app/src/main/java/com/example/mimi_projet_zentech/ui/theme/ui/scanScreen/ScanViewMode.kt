package com.example.mimi_projet_zentech.ui.theme.ui.scanScreen

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.SessionManager
import com.example.mimi_projet_zentech.data.local.SlugManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.LocationRepository
import com.example.mimi_projet_zentech.data.repository.TicketRepository
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewMode @Inject constructor(
    private val tokenManager: TokenManager  ,
    private val slugManager: SlugManager ,
    private val ticketRepository: TicketRepository ,
    private val locationRepository: LocationRepository
): ViewModel() {

//    private val tokenManager = TokenManager(getApplication())
//    private val slugManager = SlugManager(getApplication())
//    private val api by lazy {  RetrofitInstance.getPrivateApi(
//        tokenManager ,
//        onTokenExpired = { SessionManager.notifyTokenExpired()}
//    ) }

    private var _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Initializing)
    var uiState : StateFlow<ScanUiState> = _uiState.asStateFlow()
//    private  val ticketRepository  by lazy { TicketRepository(api) }
//    private val locationRepository  by lazy { LocationRepository(api) }

    var onNavigate : ((ticketNum :String  , scanStatus: ScanStatus)->Unit ) ? = null


    fun clearNavigation() {
        onNavigate = null //
        _uiState.value = ScanUiState.Ready // Reset state for the next scan
    }
    fun checkToken() {
        viewModelScope.launch {
            try {
                val slug = slugManager.getSlug()
                if (!slug.isNullOrEmpty()) {
                    locationRepository.getLocations(slug)
                }
            } catch (e: Exception) {
                Log.e("SCAN_TOKEN", "Check failed: ${e.message}")
            } finally {
                _uiState.value = ScanUiState.Ready
            }
        }
    }
    fun handleScanResult(result:String ){
        val trimmed  = result.trim()
        if(trimmed.isEmpty() || _uiState.value is ScanUiState.Verifiying) return

        viewModelScope.launch{
            _uiState.value = ScanUiState.Verifiying(ticketNumber = trimmed)

            val isLink  = trimmed.startsWith("www")|| trimmed.startsWith("http")
            val scanStatus : ScanStatus  = if(isLink) {
                ScanStatus.NOT_FOUND
            }else{
                try {
                    val response = ticketRepository.checkTicket(trimmed)
                    if (response.isSuccessful) {
                        val ticket = response.body()
                        when {
                            ticket == null -> ScanStatus.NOT_FOUND
                            ticket.nbOfChecks > 1 -> ScanStatus.ALREADY_SCANNED
                            else -> ScanStatus.VALID
                        }
                    } else ScanStatus.NOT_FOUND
                } catch (e: Exception) {
                    Log.e("SCAN_ERROR", "Reason: ${e.message}")
                    ScanStatus.NOT_FOUND
                }
            }
            val saferesult = Uri.encode(trimmed)
            onNavigate?.invoke(saferesult ,scanStatus )

        }
    }

}