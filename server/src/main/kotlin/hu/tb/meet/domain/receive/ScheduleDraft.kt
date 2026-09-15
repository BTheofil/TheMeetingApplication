package hu.tb.meet.domain.receive

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDay(
    val coachId: Int,
    val date: LocalDate,
    val drafts: List<Draft>
)

@Serializable
data class Draft(
    val start: LocalTime,
    val end: LocalTime
)
