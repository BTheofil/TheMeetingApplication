package hu.tb.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)
