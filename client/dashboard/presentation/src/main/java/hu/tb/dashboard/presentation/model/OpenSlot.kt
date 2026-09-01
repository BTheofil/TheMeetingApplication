package hu.tb.dashboard.presentation.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class OpenSlot(
    val coachId: String,
    val date: LocalDate,
    val start: LocalTime,
    val durationMinutes: Int
) {
    val end: LocalTime get() = start.plusMinutes(durationMinutes)
}
