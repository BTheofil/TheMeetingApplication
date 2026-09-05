package hu.tb.meet.domain.send

import kotlinx.serialization.Serializable

@Serializable
data class CoachResult(
    val coachId: String,
    val coachName: String,
    val status: SubscriptionStatus
)
