package hu.tb.notification.domain

import androidx.compose.runtime.Immutable

@Immutable
data class RequestNotification(
    val id: String,
    val senderName: String
)
