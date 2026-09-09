package hu.tb.data.notification

import kotlinx.serialization.Serializable

@Serializable
data class ResolveRequestSend(
    val normalId: Int
)
