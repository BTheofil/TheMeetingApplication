package hu.tb.notification.domain

data class PendingRequestsResult(
    val requests: List<RequestNotification> = emptyList(),
    val errorMessage: String? = null
)
