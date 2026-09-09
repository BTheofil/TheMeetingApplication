package hu.tb.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.data.notification.RequestAnswer
import hu.tb.network.repository.NotificationRepository
import hu.tb.notification.domain.RequestDecision
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            notificationRepository.getNotifications()
        }
    }

    fun requestMade(decision: RequestDecision, notificationId: String) {
        viewModelScope.launch {
            notificationRepository.sendRequest(decision.toNetwork(), notificationId.toInt())
        }
    }

    private fun RequestDecision.toNetwork(): RequestAnswer =
        when (this) {
            RequestDecision.ACCEPT -> RequestAnswer.ACCEPT
            RequestDecision.REJECT -> RequestAnswer.REJECT
        }
}