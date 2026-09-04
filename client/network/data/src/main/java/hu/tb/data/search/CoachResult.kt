package hu.tb.data.search

import kotlinx.serialization.Serializable

@Serializable
data class CoachResult(
    val coachId: String,
    val coachName: String,
    val status: String
)
