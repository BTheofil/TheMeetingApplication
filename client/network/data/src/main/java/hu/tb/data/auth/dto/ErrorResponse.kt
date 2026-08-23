package hu.tb.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String
)
