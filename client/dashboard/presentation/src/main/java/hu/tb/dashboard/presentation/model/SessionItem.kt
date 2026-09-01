package hu.tb.dashboard.presentation.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class SessionItem(
    val id: String,
    val title: String,
    val counterpartName: String,
    val date: LocalDate,
    val start: LocalTime,
    val durationMinutes: Int,
    val isNext: Boolean = false
) {
    val end: LocalTime get() = start.plusMinutes(durationMinutes)
}
