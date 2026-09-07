package hu.tb.search.domain

data class RequestCoachResult(
    val isRequestSent: Boolean = false,
    val errorMessage: String? = null
)
