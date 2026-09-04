package hu.tb.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.network.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    fun searchForCoach(name: String) {
        val query = name.trim()
        if (query.isBlank() || state.value.isLoading) return

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = searchRepository.searchCoach(query)

            _state.update {
                it.copy(
                    isLoading = false,
                    searchResult = result.coaches,
                    errorMessage = result.errorMessage
                )
            }
        }
    }
}
