package hu.tb.domain

data class AuthResults(
    val token: String? = null,
    val errorMessage: String? = null
)
