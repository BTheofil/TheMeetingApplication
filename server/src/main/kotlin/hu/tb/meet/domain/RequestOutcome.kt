package hu.tb.meet.domain

import hu.tb.meet.domain.send.SubscriptionStatus

data class RequestOutcome(
    val normalId: Int,
    val previousStatus: SubscriptionStatus,
    val currentStatus: SubscriptionStatus,
)
