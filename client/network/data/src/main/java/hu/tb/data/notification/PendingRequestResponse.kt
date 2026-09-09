package hu.tb.data.notification

import kotlinx.serialization.Serializable

@Serializable
data class PendingRequestResponse(
    val normalId: String,
    val normalName: String
)
