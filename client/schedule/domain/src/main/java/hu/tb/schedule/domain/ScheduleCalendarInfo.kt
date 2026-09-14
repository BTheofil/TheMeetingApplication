package hu.tb.schedule.domain

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

@Stable
data class ScheduleCalendarInfo(
    val visibleMonth: YearMonth,
    val selectedDate: LocalDate,
    val today: LocalDate
)
