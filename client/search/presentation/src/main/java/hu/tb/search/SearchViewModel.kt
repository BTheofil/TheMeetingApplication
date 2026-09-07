package hu.tb.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.network.repository.SearchRepository
import hu.tb.search.domain.Status
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _event = Channel<String>()
    val event = _event.receiveAsFlow()

    fun action(action: SearchAction) {
        when (action) {
            is SearchAction.OnCoachAddRequest -> requestCoach(action.coachId)
            is SearchAction.OnSearch -> searchForCoach(action.query)
            else -> {}
        }
    }

    private fun searchForCoach(query: String) {
        val query = query.trim()
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

    private fun requestCoach(coachId: String) {
        updateCoachStatus(coachId, Status.PENDING)

        viewModelScope.launch {
            val result = searchRepository.requestCoach(coachId)

            if (!result.isRequestSent) {
                updateCoachStatus(coachId, Status.INIT)
                result.errorMessage?.let { _event.send(it) }
            }
        }
    }

    private fun updateCoachStatus(coachId: String, status: Status) {
        _state.update { state ->
            state.copy(
                searchResult = state.searchResult.map { coach ->
                    if (coach.id == coachId) coach.copy(status = status) else coach
                }
            )
        }
    }
}
