package hu.tb.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String
)
