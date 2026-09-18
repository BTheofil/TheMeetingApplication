package hu.tb.meet.domain.send

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class SessionResult(
    val id: Int,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
    // the other party of a booked session: the booker for a coach, the coach for a normal account
    val counterpart: String? = null
)
