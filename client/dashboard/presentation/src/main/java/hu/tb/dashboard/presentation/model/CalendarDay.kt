package hu.tb.dashboard.presentation.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class CalendarDay(
    val date: LocalDate,
    val sessionCount: Int = 0,
    val hasOpenSlot: Boolean = false
)

@Immutable
data class CalendarWeek(
    val days: List<CalendarDay>
)

@Immutable
data class CalendarMonth(
    val weeks: List<CalendarWeek>
)
