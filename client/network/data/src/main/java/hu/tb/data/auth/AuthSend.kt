package hu.tb.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthSend(
    val username: String,
    val password: String,
    val type: String
)
