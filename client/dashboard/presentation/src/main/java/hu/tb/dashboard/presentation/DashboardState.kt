package hu.tb.dashboard.presentation

import androidx.compose.runtime.Immutable
import hu.tb.dashboard.presentation.model.CalendarDay
import hu.tb.dashboard.presentation.model.CoachItem
import hu.tb.dashboard.presentation.model.OpenSlot
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.dashboard.presentation.util.currentDate
import hu.tb.domain.ProfileType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth

@Immutable
data class DashboardState(
    val profileType: ProfileType = ProfileType.NORMAL,
    val today: LocalDate = currentDate(),
    val selectedDate: LocalDate = today,
    val visibleMonth: YearMonth = today.yearMonth,
    val days: List<CalendarDay> = emptyList(),
    val sessionsOnSelectedDay: List<SessionItem> = emptyList(),
    val openSlotsOnSelectedDay: List<OpenSlot> = emptyList(),
    val coaches: List<CoachItem> = emptyList(),
    val availableSlots: List<OpenSlot> = emptyList(),
    val isLoading: Boolean = false
) {
    fun dayOf(date: LocalDate): CalendarDay =
        days.firstOrNull { it.date == date } ?: CalendarDay(date)

    fun coachNameOf(coachId: String): String? =
        coaches.firstOrNull { it.id == coachId }?.name
}
