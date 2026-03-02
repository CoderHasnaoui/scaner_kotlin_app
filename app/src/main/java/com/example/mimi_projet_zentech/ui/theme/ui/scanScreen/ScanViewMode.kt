package com.example.mimi_projet_zentech.ui.theme.ui.scanScreen

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.ui.theme.data.local.SessionManager
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.data.model.Enum.ScanStatus
import com.example.mimi_projet_zentech.ui.theme.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository
import com.example.mimi_projet_zentech.ui.theme.data.repository.LocationRepository
import com.example.mimi_projet_zentech.ui.theme.data.repository.TicketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class ScanViewMode(application: Application): AndroidViewModel(application) {
    private val tokenManager = TokenManager(getApplication())
    private val api = RetrofitInstance.getPrivateApi(
        tokenManager ,
        onTokenExpired = { SessionManager.notifyTokenExpired()}
    )
    var isReady by mutableStateOf(false)
        private set
    private  val ticketRepository  = TicketRepository(api)

    private val locationRepository = LocationRepository(api)

    var isProcessing by  mutableStateOf(false)


    var onNavigate : ((ticketNum :String  , scanStatus: ScanStatus)->Unit ) ? = null
    fun resetState() {
        isProcessing = false  //  reset when screen opens
    }


    fun checkToken() {
        viewModelScope.launch {
            try {
                val slug = tokenManager.getSlug()
                if (!slug.isNullOrEmpty()) {
                    locationRepository.getLocations(slug)
                }
            } catch (e: Exception) { }
            finally {
                isReady = true
            }
        }
    }
    fun handleScanResult(result:String ){
        val trimmed  = result.trim()
        if(trimmed.isEmpty() || isProcessing) return
            isProcessing = true
        viewModelScope.launch{
            val isLink  = trimmed.startsWith("www")|| trimmed.startsWith("http")
            val scanStatus : ScanStatus  = if(isLink) {
                ScanStatus.NOT_FOUND
            }else{
                try{
                    val response = ticketRepository.checkTicket(trimmed)
                    if(response.isSuccessful){
                       val  ticket = response.body()
                        if(ticket!=null){
                            if(ticket.isScanned ==  true ){
                                ScanStatus.ALREADY_SCANNED
                            }else{
                                ScanStatus.VALID
                            }
                        }else ScanStatus.NOT_FOUND

                    }else ScanStatus.NOT_FOUND
                }catch (e : Exception){ScanStatus.NOT_FOUND }
            }
            val saferesult = Uri.encode(trimmed)
            onNavigate?.invoke(saferesult ,scanStatus )
            isProcessing = false
        }
    }

}