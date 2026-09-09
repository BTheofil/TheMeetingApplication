package hu.tb.notification.presentation

import hu.tb.notification.domain.RequestDecision

sealed interface NotificationAction {
    data object BackRequest : NotificationAction
    data object RetryRequest : NotificationAction
    data class RequestResolved(val decision: RequestDecision, val requestId: String) :
        NotificationAction
}
