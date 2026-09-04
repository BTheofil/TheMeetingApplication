package hu.tb.search.domain

data class SearchResult(
    val coaches: List<Coach>,
    val errorMessage: String? = null
)
