package hu.tb.meet.domain.send

import hu.tb.meet.domain.receive.AccountType
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: Int,
    val username: String,
    val type: AccountType
)
