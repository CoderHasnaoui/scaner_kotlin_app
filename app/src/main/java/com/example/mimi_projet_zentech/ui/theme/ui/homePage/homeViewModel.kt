package com.example.mimi_projet_zentech.ui.theme.ui.homePage
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mimi_projet_zentech.data.local.SessionManager
import com.example.mimi_projet_zentech.data.local.SlugManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.local.db.DatabaseProvider
import com.example.mimi_projet_zentech.data.local.entity.merchantEntity.LocationEntity
import com.example.mimi_projet_zentech.data.local.entity.relation.GroupeWithLocation
import com.example.mimi_projet_zentech.data.model.GroupeMerchant.Location
import com.example.mimi_projet_zentech.data.model.GroupeMerchant.MerchantGroup
import com.example.mimi_projet_zentech.data.remote.RetrofitInstance
import com.example.mimi_projet_zentech.data.repository.MerchantRepository
import com.example.mimi_projet_zentech.ui.theme.ui.homePage.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: MerchantRepository ,
    val slugManager: SlugManager ,

) : ViewModel() {

//    private val db = DatabaseProvider.getDatabase(application)
//    private val tokenManager = TokenManager(application)
//    val slugManager = SlugManager(application)

    // 1.sealedState
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // 2. inputSrarch
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()


    private var allMerchants = emptyList<MerchantGroup>()

//    private val merchantApi by lazy {
//        RetrofitInstance.getPrivateApi(tokenManager) { SessionManager.notifyTokenExpired() }
//    }
//    private val repo by lazy { MerchantRepository(merchantApi , db.merchantDao()) }
    val merchantsPaged : Flow<PagingData<GroupeWithLocation>> = repo
        .getMetchantsPages()
        .cachedIn(viewModelScope)


    val expandedCardIds = mutableStateMapOf<String, Boolean>()
    val expandedLocationIds = mutableStateMapOf<String, Boolean>()

    init{
//        observeRoomData()
    }

    private fun observeRoomData() {
        viewModelScope.launch {
            combine(
                repo.allMerchantFlow.onStart { emit(emptyList()) },
                _searchText
            ) { roomList, query ->
                val uiList = roomList.map { it.toMerchantGroup() }
                Pair(uiList, query)
            }.collect { (merchants, query) ->
                allMerchants = merchants
                updateUiState(merchants, query)
            }
        }
    }
    private fun updateUiState(merchants: List<MerchantGroup>, query: String) {
        if (_uiState.value is HomeUiState.Redirecting) return
        if (_uiState.value is HomeUiState.Error) return
        if (merchants.isEmpty()) {

            if (_uiState.value !is HomeUiState.Loading) {
                _uiState.value = HomeUiState.Empty
            }
            return
        }

        val filtered = merchants.filter {
            it.name.contains(query, ignoreCase = true)
        }

        _uiState.value = when {
            filtered.isEmpty() && query.isNotEmpty() -> HomeUiState.NoResults
            else -> HomeUiState.Success(merchants = filtered  )
        }
    }
    fun checkInitialState(forceSelect: Boolean, onRedirect: (String) -> Unit) {
        val savedSlug = slugManager.getSlug()

        if (!forceSelect && !savedSlug.isNullOrEmpty()) {
            _uiState.value = HomeUiState.Redirecting
            onRedirect(savedSlug)
            return
        }

        viewModelScope.launch {
            // Use Dispatchers.IO for the database query
            val localMerchants = withContext(Dispatchers.IO) {
                try {
                    repo.getCurrentMerchantsOnce()
                } catch (e: Exception) {
                    emptyList()
                }
            }

            if (localMerchants.isNotEmpty()) {
                // Case A: Exactly 1 merchant in Room automaticly Refirct
                if (localMerchants.size == 1 && !forceSelect) {
                    _uiState.value = HomeUiState.Redirecting
                    val slug = localMerchants[0].merchantGroup.slug
                    slugManager.saveSlug(slug)
                    withContext(Dispatchers.Main) {
                        onRedirect(slug)
                    }
                } else {
                    // Case B: Multiple merchants  Show the list et  sync api
                    observeRoomData()
                    launch(Dispatchers.IO) {
                        try {
                            repo.refrechMerchant()
                        } catch (e: Exception) {
                            println("Background sync failed (Silent): ${e.message}")
                        }
                    }
                }
            } else {
                // Case C: Room is Vide : load from the aPi.
                loadMerchants(forceSelect, onRedirect)
            }
        }
    }
    fun loadMerchants(forceSelect: Boolean = false, onRedirect: (String) -> Unit) {

        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
        // chechk localeement
            val localData = withContext(Dispatchers.IO) {
                try {
                    repo.getCurrentMerchantsOnce()
                } catch (e: Exception) {
                    emptyList()
                }
            }


            try {
                withContext(Dispatchers.IO) {
                    repo.refrechMerchant()
                }
            } catch (e: Exception) {

                println("Network sync failed: ${e.message}")
            }


            withContext(Dispatchers.Main) {
                val finalData = withContext(Dispatchers.IO) { repo.getCurrentMerchantsOnce() }
                if (finalData.isNotEmpty()) {
                    if (finalData.size == 1 && !forceSelect) {
                        val slug = finalData[0].merchantGroup.slug

                        slugManager.saveSlug(slug)
                        onRedirect(slug)

                    } else{
                        observeRoomData()
                    }
                } else {
                    _uiState.value = HomeUiState.Error("No data. Check internet.")
                }
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
    fun GroupeWithLocation.toMerchantGroup(): MerchantGroup {
        return MerchantGroup(
            name = this.merchantGroup.name,
            slug = this.merchantGroup.slug,
            locations = this.location.map { it.toLocation() }?:emptyList()
        )
    }
    fun LocationEntity.toLocation(): Location {
        return Location(

            name = this.name
        )
    }


    val showSearchField: Boolean get() = allMerchants.size > 3 || _searchText.value.isNotEmpty()
}