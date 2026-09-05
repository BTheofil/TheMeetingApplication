package hu.tb.meet.domain.send

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultResponse(
    val coaches: List<CoachResult>
)
