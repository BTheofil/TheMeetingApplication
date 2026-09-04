package hu.tb.search

import androidx.compose.runtime.Stable
import hu.tb.search.domain.Coach

@Stable
data class SearchState(
    val isLoading: Boolean = false,
    val searchResult: List<Coach> = emptyList(),
    val errorMessage: String? = null
)
