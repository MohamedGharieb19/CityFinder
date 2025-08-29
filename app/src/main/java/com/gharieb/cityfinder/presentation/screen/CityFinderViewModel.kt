package com.gharieb.cityfinder.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gharieb.cityfinder.domain.usecase.SearchCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val searchCitiesUseCase: SearchCitiesUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _querySearch = MutableStateFlow("")
    val querySearch = _querySearch.asStateFlow()

    private val _result = MutableStateFlow(SearchCitiesUseCase.Result.empty())
    val result = _result.asStateFlow()

    init {
        viewModelScope.launch {
            performSearch("")
            _isLoading.value = false
        }
    }

    fun onEvent(event: CityFinderEvents) {
        when (event) {
            is CityFinderEvents.OnQuerySearchChanged -> onQuerySearchChanged(event.querySearch)
        }
    }

    private fun onQuerySearchChanged(querySearch: String) {
        _querySearch.value = querySearch
        viewModelScope.launch { performSearch(querySearch) }
    }

    private suspend fun performSearch(querySearch: String) {
        val result = searchCitiesUseCase(querySearch)
        _result.value = result
    }
}
