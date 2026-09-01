package hu.tb.search

import androidx.compose.runtime.Stable
import hu.tb.search.domain.CoachResult

@Stable
data class SearchState(
    val searchResult: List<CoachResult> = emptyList()
)
