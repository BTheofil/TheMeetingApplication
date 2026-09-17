package hu.tb.data.schedule

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class DeleteSessionSend(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime
)
