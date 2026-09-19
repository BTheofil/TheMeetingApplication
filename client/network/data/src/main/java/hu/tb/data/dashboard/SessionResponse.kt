package hu.tb.data.dashboard

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    val id: Int,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
    // only a booked session has one: the coach for a normal account
    val counterpart: String? = null
)
