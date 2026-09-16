package hu.tb.meet.domain.receive

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class SlotReceive(
    val coachId: Int,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime
)
