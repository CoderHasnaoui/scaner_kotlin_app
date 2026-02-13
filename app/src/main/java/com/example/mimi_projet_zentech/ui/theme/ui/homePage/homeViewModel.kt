package com.example.mimi_projet_zentech.ui.theme.ui.homePage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mimi_projet_zentech.ui.theme.data.model.BusinessGroup
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel : ViewModel() {
    private val repository = HomeRepository()
    // NEW: UI State for Expanded Cards (using IDs to track)
    var expandedCardIds = mutableStateMapOf<Int, Boolean>()
    var expandedLocationIds = mutableStateMapOf<Int, Boolean>()


    var businessGroups by mutableStateOf<List<BusinessGroup>>(emptyList())
        private set

     var searchText by mutableStateOf("")
        private set

    init {
        loadBusinessGroups()
    }

    private fun loadBusinessGroups() {
        businessGroups = repository.getBusinessGroups()


    }

    fun onSearchChange(text: String) {
        searchText = text
    }
    // Business Logic for expansion
    fun toggleOfficeExpansion(id: Int) {
        val current = expandedCardIds[id] ?: false
        expandedCardIds[id] = !current
    }
    fun toggleLocationList(id: Int) {
        val current = expandedLocationIds[id] ?: false
        expandedLocationIds[id] = !current
    }

    val filteredBusinessGroups: List<BusinessGroup>
        get() = businessGroups.filter {
            it.name.contains(searchText, ignoreCase = true)
        }
    val showSearchField: Boolean get() =  businessGroups.size > 3 || searchText.isNotEmpty()

    val isEmpty: Boolean
        get() = businessGroups.isEmpty()

    val noSearchResult: Boolean
        get() = searchText.isNotEmpty() && filteredBusinessGroups.isEmpty()
}
