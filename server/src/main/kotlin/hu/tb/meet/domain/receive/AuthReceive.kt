package hu.tb.meet.domain.receive

import kotlinx.serialization.Serializable

@Serializable
data class AuthReceive(
    val username: String,
    val password: String,
    val type: AccountType
)
