package hu.tb.meet.domain.send

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SubscriptionStatus {
    @SerialName("init") INIT,
    @SerialName("pending") PENDING,
    @SerialName("accepted") ACCEPTED
}
