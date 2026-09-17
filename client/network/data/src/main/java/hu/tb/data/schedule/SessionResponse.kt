package hu.tb.data.schedule

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    val id: Int,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime
)
