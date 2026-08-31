package hu.tb.dashboard.presentation.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.LocalTime

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
    val end: LocalTime get() = start.plusMinutes(durationMinutes.toLong())
}

@Immutable
data class OpenSlot(
    val id: String,
    val coachId: String,
    val date: LocalDate,
    val start: LocalTime,
    val durationMinutes: Int
) {
    val end: LocalTime get() = start.plusMinutes(durationMinutes.toLong())
}

@Immutable
data class CoachItem(
    val id: String,
    val name: String,
    val openHourCount: Int
) {
    val initials: String
        get() = name.split(' ')
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
}

@Immutable
data class CalendarDay(
    val date: LocalDate,
    val sessionCount: Int = 0,
    val hasOpenSlot: Boolean = false
)
