package hu.tb.data.dashboard

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class FreeSessionSend(
    val coachId: Int,
    val date: LocalDate
)
