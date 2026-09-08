package hu.tb.notification.presentation

sealed interface NotificationAction {
    data object OnBackClick : NotificationAction
    data class OnRequestAccept(val notificationId: String) : NotificationAction
    data class OnRequestReject(val notificationId: String) : NotificationAction
}
