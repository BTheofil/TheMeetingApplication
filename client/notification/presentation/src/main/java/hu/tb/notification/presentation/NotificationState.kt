package hu.tb.notification.presentation

import androidx.compose.runtime.Stable
import hu.tb.notification.domain.RequestNotification

@Stable
data class NotificationState(
    val isLoading: Boolean = false,
    val requests: List<RequestNotification> = emptyList()
)
