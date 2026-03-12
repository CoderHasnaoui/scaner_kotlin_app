import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)
    private val tokenManager = TokenManager(application)
    private val slugManager = SlugManager(application)

    // 1.sealedState
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // 2. inputSrarch
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()


    private var allMerchants = emptyList<MerchantGroup>()

    private val merchantApi by lazy {
        RetrofitInstance.getPrivateApi(tokenManager) { SessionManager.notifyTokenExpired() }
    }
    private val repo by lazy { MerchantRepository(merchantApi , db.merchantDao()) }

    // ui Exp States
    val expandedCardIds = mutableStateMapOf<String, Boolean>()
    val expandedLocationIds = mutableStateMapOf<String, Boolean>()

    init{
        observeRoomData()
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

        // 1. Immediate redirect if a selection already exists
        if (!forceSelect && !savedSlug.isNullOrEmpty()) {
            onRedirect(savedSlug)
            return
        }

        // 2. Start checking Room and Network
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
                // Case A: Exactly 1 merchant in Room -> Auto-redirect
                if (localMerchants.size == 1 && !forceSelect) {
                    val slug = localMerchants[0].merchantGroup.slug
                    slugManager.saveSlug(slug)
                    withContext(Dispatchers.Main) {
                        onRedirect(slug)
                    }
                } else {
                    // Case B: Multiple merchants -> Show the list AND sync in background
                    // We use a separate launch so the UI doesn't wait for the network to finish
                    launch(Dispatchers.IO) {
                        try {
                            repo.refrechMerchant()
                        } catch (e: Exception) {
                            println("Background sync failed (Silent): ${e.message}")
                        }
                    }
                }
            } else {
                // Case C: Room is totally empty! We must load from the API.
                loadMerchants(forceSelect, onRedirect)
            }
        }
    }
    fun loadMerchants(forceSelect: Boolean = false, onRedirect: (String) -> Unit) {

        viewModelScope.launch {
            // 1. SAFE LOCAL CHECK
            val localData = withContext(Dispatchers.IO) {
                try {
                    repo.getCurrentMerchantsOnce()
                } catch (e: Exception) {
                    emptyList() // If Room fails, return empty instead of crashing
                }
            }

            // 2. NETWORK SYNC (Trapped in a try/catch)
            try {
                withContext(Dispatchers.IO) {
                    repo.refrechMerchant()
                }
            } catch (e: Exception) {
                // Log it, but don't crash
                println("Network sync failed: ${e.message}")
            }

            // 3. UI UPDATE
            withContext(Dispatchers.Main) {
                val finalData = withContext(Dispatchers.IO) { repo.getCurrentMerchantsOnce() }
                if (finalData.isNotEmpty()) {
                    if (finalData.size == 1 && !forceSelect) {
                        onRedirect(finalData[0].merchantGroup.slug)
                    }
                    // Your Flow in observeRoomData will handle the Success state
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


    // This stays accurate because it looks at the full cached list
    val showSearchField: Boolean get() = allMerchants.size > 3 || _searchText.value.isNotEmpty()
}