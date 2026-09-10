package hu.tb.data.dashboard

import kotlinx.serialization.Serializable

@Serializable
data class MyCoachResponse(
    val coachId: String,
    val coachName: String,
    val status: String
)
