package hu.tb.data.search

import kotlinx.serialization.Serializable

@Serializable
data class CoachRequestSend(
    val coachId: Int
)
