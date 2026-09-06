package hu.tb.data.search

import kotlinx.serialization.Serializable

@Serializable
data class CoachResultResponse(
    val coaches: List<CoachDto>
)

@Serializable
data class CoachDto(
    val coachId: String,
    val coachName: String,
    val status: String
)

