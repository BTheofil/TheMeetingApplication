package hu.tb.meet.domain.send

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class SessionResult(
    val id: Int,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime
)
