package hu.tb.dashboard.presentation

import androidx.compose.runtime.Immutable
import hu.tb.dashboard.domain.CoachItem
import hu.tb.dashboard.domain.OpenSlot
import hu.tb.dashboard.domain.SessionItem
import hu.tb.dashboard.presentation.util.currentDate
import hu.tb.datastore.ProfileType
import kotlinx.datetime.LocalDate

@Immutable
data class DashboardState(
    val profileType: ProfileType? = null,
    val today: LocalDate = currentDate(),
    val selectedDate: LocalDate = today,
    val sessions: List<SessionItem> = emptyList(),
    val openSlots: List<OpenSlot> = emptyList(),
    val isMyCoachesLoading: Boolean = false,
    val myCoaches: List<CoachItem> = emptyList()
) {
    fun sessionsOn(date: LocalDate): List<SessionItem> =
        sessions.filter { it.date == date }.sortedBy { it.start }

    fun openSlotsOn(date: LocalDate): List<OpenSlot> =
        openSlots.filter { it.date == date }.sortedBy { it.start }

    fun coachNameOf(coachId: String): String? =
        myCoaches.firstOrNull { it.id == coachId }?.name
}
