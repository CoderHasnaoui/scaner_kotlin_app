package com.example.mimi_projet_zentech.ui.theme.ui.homePage

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.ui.theme.data.local.SessionManager
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.data.model.BusinessGroup
import com.example.mimi_projet_zentech.ui.theme.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.ui.theme.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.ui.theme.data.repository.AuthRepository
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository
import com.example.mimi_projet_zentech.ui.theme.data.repository.MerchantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel (application : Application): AndroidViewModel(application){

    private  val tokenManager = TokenManager(application)
    var merchantList by mutableStateOf<List<MerchantGroup>>(emptyList())
    var isLoading by mutableStateOf(false)
    private val merchantApi = RetrofitInstance.getPrivateApi(tokenManager ,  onTokenExpired = { SessionManager.notifyTokenExpired()} )
    private val repo = AuthRepository(merchantApi)


    var expandedCardIds = mutableStateMapOf<String, Boolean>()

    var expandedLocationIds = mutableStateMapOf<String, Boolean>()


//    var businessGroups by mutableStateOf<List<BusinessGroup>>(emptyList())
//        private set

     var searchText by mutableStateOf("")
        private set

    init {
//        loadBusinessGroups()
            loadMerchants() // Fetch as soon as the Home Page opens

    }

    private fun loadBusinessGroups() {
//        businessGroups = repository.getBusinessGroups()


    }
    fun loadMerchants(
        forceSelect: Boolean = false,  // 👈 add this
        onSingleMerchant: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repo.fetchMerchnat()
                if (response.isSuccessful) {
                    merchantList = response.body() ?: emptyList()

                    if (merchantList.size == 1 && !forceSelect) {  // 👈 skip if forceSelect
                        val slug = merchantList[0].slug
                        tokenManager.saveSlug(slug)
                        onSingleMerchant(slug)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    fun onSearchChange(text: String) {
        searchText = text
    }
    // Business Logic for expansion
    fun toggleOfficeExpansion(slug: String) {
        val current = expandedCardIds[slug] ?: false
        expandedCardIds[slug] = !current
    }
    fun toggleLocationList(slug: String) {
        val current = expandedLocationIds[slug] ?: false
        expandedLocationIds[slug] = !current
    }

    val filteredBusinessGroups: List<MerchantGroup>
        get() = merchantList.filter {
            it.name.contains(searchText, ignoreCase = true)
        }
    val showSearchField: Boolean get() =  merchantList.size > 3 || searchText.isNotEmpty()

    val isEmpty: Boolean
        get() = merchantList.isEmpty()

    val noSearchResult: Boolean
        get() = searchText.isNotEmpty() && filteredBusinessGroups.isEmpty()
}
