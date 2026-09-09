package hu.tb.notification.domain

data class ResolveRequestResult(
    val isResolved: Boolean = false,
    val errorMessage: String? = null
)
