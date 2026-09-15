package hu.tb.schedule.presentation

import androidx.compose.runtime.Immutable
import hu.tb.schedule.domain.DraftSlot
import hu.tb.schedule.domain.TimeSlot
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
    val slotsByDate: Map<LocalDate, List<TimeSlot>> = emptyMap(),
    val drafts: Map<LocalDate, List<DraftSlot>> = emptyMap(),
    val clipboard: List<DraftSlot> = emptyList(),
    val isCalendarLoading: Boolean = false
)
