package hu.tb.meet.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class RequestReceive(
    val coachId: Int
)
