package com.example.mimi_projet_zentech.ui.theme.ui.homePage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mimi_projet_zentech.ui.theme.data.model.BusinessGroup
import com.example.mimi_projet_zentech.ui.theme.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel : ViewModel() {
    private val repository = HomeRepository()

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
