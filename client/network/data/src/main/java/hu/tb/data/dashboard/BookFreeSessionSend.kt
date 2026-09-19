package hu.tb.data.dashboard

import kotlinx.serialization.Serializable

@Serializable
data class BookFreeSessionSend(
    val sessionId: Int
)
