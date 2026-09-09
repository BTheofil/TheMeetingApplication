package hu.tb.notification.presentation

import hu.tb.notification.domain.RequestDecision

sealed interface NotificationAction {
    data object BackRequest : NotificationAction
    data class RequestSelected(val decision: RequestDecision, val notificationId: String) :
        NotificationAction
}
