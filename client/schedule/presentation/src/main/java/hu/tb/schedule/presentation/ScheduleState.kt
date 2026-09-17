package hu.tb.schedule.presentation

import androidx.compose.runtime.Immutable
import hu.tb.schedule.domain.TimeRange
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import kotlin.time.Clock

@Immutable
data class ScheduleState(
    val selectedDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val visibleMonth: YearMonth = selectedDate.yearMonth,
    val sessionsByDate: Map<LocalDate, List<TimeRange.SessionTime>> = emptyMap(),
    val draftsByDate: Map<LocalDate, List<TimeRange.DraftSlot>> = emptyMap(),
    val isCalendarLoading: Boolean = false
)
