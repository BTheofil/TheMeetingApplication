package hu.tb.meet.domain.send

import kotlinx.serialization.Serializable

@Serializable
data class SubscriberResult(
    val normalId: String,
    val normalName: String
)
