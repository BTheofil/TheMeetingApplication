package hu.tb.meet.domain.get

import kotlinx.serialization.Serializable

@Serializable
data class LoginReceive(
    val username: String,
    val password: String,
    val type: String
)
