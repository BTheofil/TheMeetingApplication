package hu.tb.meet.domain.receive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AccountType {
    @SerialName("coach") COACH,
    @SerialName("normal") NORMAL
}
