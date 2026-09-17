package hu.tb.data.schedule

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class DraftDaySend(
    val date: LocalDate,
    val drafts: List<DraftSend>
)

@Serializable
data class DraftSend(
    val start: LocalTime,
    val end: LocalTime
)
