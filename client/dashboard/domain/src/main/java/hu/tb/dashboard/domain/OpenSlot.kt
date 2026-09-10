package hu.tb.dashboard.domain

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class OpenSlot(
    val coachId: String,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
    val durationMinutes: Int
)