package hu.tb.dashboard.presentation

import androidx.compose.runtime.Immutable
import hu.tb.dashboard.presentation.model.CalendarDay
import hu.tb.dashboard.presentation.model.CoachItem
import hu.tb.dashboard.presentation.model.OpenSlot
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.dashboard.presentation.util.currentDate
import hu.tb.domain.ProfileType
import kotlinx.datetime.LocalDate

@Immutable
data class DashboardState(
    val profileType: ProfileType = ProfileType.NORMAL,
    val today: LocalDate = currentDate(),
    val selectedDate: LocalDate = today,
    val sessions: List<SessionItem> = emptyList(),
    val openSlots: List<OpenSlot> = emptyList(),
    val coaches: List<CoachItem> = emptyList()
) {
    fun dayOf(date: LocalDate): CalendarDay =
        CalendarDay(
            date = date,
            sessionCount = sessions.count { it.date == date },
            hasOpenSlot = openSlots.any { it.date == date }
        )

    fun sessionsOn(date: LocalDate): List<SessionItem> =
        sessions.filter { it.date == date }.sortedBy { it.start }

    fun openSlotsOn(date: LocalDate): List<OpenSlot> =
        openSlots.filter { it.date == date }.sortedBy { it.start }

    fun coachNameOf(coachId: String): String? =
        coaches.firstOrNull { it.id == coachId }?.name
}
