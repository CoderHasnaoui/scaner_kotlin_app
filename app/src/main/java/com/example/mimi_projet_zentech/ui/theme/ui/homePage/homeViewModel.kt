package com.example.mimi_projet_zentech.ui.theme.ui.homePage

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimi_projet_zentech.data.local.SessionManager
import com.example.mimi_projet_zentech.data.local.SlugManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.MerchantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class HomeViewModel (application : Application): AndroidViewModel(application){
    var isInitializing by mutableStateOf(true)
        private set

    private  val tokenManager = TokenManager(application)
     private var slugManager = SlugManager(application)

    private var _merchantList = MutableStateFlow<List<MerchantGroup>>(emptyList())
    var merchantList  : StateFlow<List<MerchantGroup>> = _merchantList.asStateFlow()
    var isLoading by mutableStateOf(false)
    private val merchantApi by lazy { RetrofitInstance.getPrivateApi(tokenManager ,  onTokenExpired = { SessionManager.notifyTokenExpired()} )}
    private val repo by lazy { MerchantRepository(merchantApi) }

    var expandedCardIds = mutableStateMapOf<String, Boolean>()

    var expandedLocationIds = mutableStateMapOf<String, Boolean>()
    private var _searchText = MutableStateFlow<String>("")
    var searchText : StateFlow<String> = _searchText.asStateFlow()

    fun selectMerchant(slug: String) {
        slugManager.saveSlug(slug)
    }
    fun checkInitialState(forceSelect: Boolean, onRedirect: (String) -> Unit) {
        val savedSlug = slugManager.getSlug()

        // have slug and don't force ui to build
        if (!forceSelect && !savedSlug.isNullOrEmpty()) {
            onRedirect(savedSlug)
            return
        }

        // Load List
        loadMerchants(forceSelect, onRedirect)
    }
    fun loadMerchants(
        forceSelect: Boolean = false,
        onRedirect: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repo.getMerchants()
                if (response.isSuccessful) {
                    val list  = response.body() ?: emptyList()
                        _merchantList.value = list
                    if (list.size == 1 && !forceSelect) {  //   forceSelect
                        val slug = list[0].slug
                        slugManager.saveSlug(slug)
                        onRedirect(slug)
                    }else{
                        isInitializing = false
                    }
                }else{
                    isInitializing = false
                }
            } catch (e: Exception) {
                isInitializing = false
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    fun onSearchChange(text: String) {
        _searchText.value = text
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

    val filteredBusinessGroups = combine(_merchantList , _searchText){list ,query->
        if(query.isEmpty())
        {
            list
        }else{
            list.filter { it.name.contains(query , ignoreCase = true) }
        }


    }.stateIn(
        scope = viewModelScope ,
        started = SharingStarted.WhileSubscribed(5000) ,
        initialValue = emptyList()
    )

    val showSearchField: Boolean get() =  _merchantList.value.size > 3 || _searchText.value.isNotEmpty()

    val noSearchResult: Boolean
        get() = _searchText.value.isNotEmpty() && filteredBusinessGroups.value.isEmpty()
}
