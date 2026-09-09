package hu.tb.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.network.repository.NotificationRepository
import hu.tb.notification.domain.RequestDecision
import hu.tb.notification.domain.RequestNotification
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    private val _event = Channel<String>()
    val event = _event.receiveAsFlow()

    init {
        loadPendingRequests()
    }

    fun action(action: NotificationAction) {
        when (action) {
            NotificationAction.RetryRequest -> loadPendingRequests()

            is NotificationAction.RequestResolved ->
                resolveRequest(action.decision, action.requestId)

            NotificationAction.BackRequest -> Unit
        }
    }

    private fun loadPendingRequests() {
        if (state.value.isLoading) return

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = notificationRepository.getPendingRequests()

            _state.update {
                it.copy(
                    isLoading = false,
                    requests = result.requests,
                    errorMessage = result.errorMessage
                )
            }

            result.errorMessage?.let { _event.send(it) }
        }
    }

    private fun resolveRequest(decision: RequestDecision, requestId: String) {
        val index = state.value.requests.indexOfFirst { it.id == requestId }
        if (index == -1) return

        val request = state.value.requests[index]
        _state.update { state ->
            state.copy(requests = state.requests - request)
        }

        viewModelScope.launch {
            val result = notificationRepository.resolveRequest(decision, requestId)

            if (!result.isResolved) {
                restoreRequest(request, index)
                result.errorMessage?.let { _event.send(it) }
            }
        }
    }

    private fun restoreRequest(request: RequestNotification, index: Int) {
        _state.update { state ->
            state.copy(
                requests = state.requests.toMutableList().apply {
                    add(index.coerceAtMost(size), request)
                }
            )
        }
    }
}
