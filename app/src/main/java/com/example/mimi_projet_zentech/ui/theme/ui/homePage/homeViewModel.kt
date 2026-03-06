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
class HomeViewModel (application : Application): AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val slugManager = SlugManager(application)

    // 1.  SEALD STATE
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // 2.  SEARCH INPUT
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // 3. THE CACHE (Keeps the full list in memory for fast filtering)
    private var allMerchants = emptyList<MerchantGroup>()

    private val merchantApi by lazy {
        RetrofitInstance.getPrivateApi(tokenManager) { SessionManager.notifyTokenExpired() }
    }
    private val repo by lazy { MerchantRepository(merchantApi) }

    // UI Expansion States
    val expandedCardIds = mutableStateMapOf<String, Boolean>()
    val expandedLocationIds = mutableStateMapOf<String, Boolean>()

    fun checkInitialState(forceSelect: Boolean, onRedirect: (String) -> Unit) {
        val savedSlug = slugManager.getSlug()
        // if i have a slug saved and don't force to it go to Scanner
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
            _uiState.value = HomeUiState.Loading
            try {
                val response = repo.getMerchants()
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    allMerchants = list
                    if (list.size == 1 && !forceSelect) {  //   forceSelect
                        val slug = list[0].slug
                        slugManager.saveSlug(slug)
                        onRedirect(slug)
                    } else {
                        applyFilter()
                    }
                } else {
                    _uiState.value = HomeUiState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Check your internet connection")
            }
        }
    }

    fun onSearchChange(text: String) {
        _searchText.value = text
        applyFilter()
    }

    private fun applyFilter() {
        val query = _searchText.value
        val filtered = allMerchants.filter { it.name.contains(query, ignoreCase = true) }

        _uiState.value = when {
            allMerchants.isEmpty() -> HomeUiState.Empty
            filtered.isEmpty() && query.isNotEmpty() -> HomeUiState.NoResults
            else -> HomeUiState.Success(filtered)
        }
    }

    fun selectMerchant(slug: String) = slugManager.saveSlug(slug)
    fun toggleOfficeExpansion(slug: String) {
        expandedCardIds[slug] = !(expandedCardIds[slug] ?: false)
    }

    fun toggleLocationList(slug: String) {
        expandedLocationIds[slug] = !(expandedLocationIds[slug] ?: false)
    }

    val showSearchField: Boolean get() = allMerchants.size > 3 || _searchText.value.isNotEmpty()
}