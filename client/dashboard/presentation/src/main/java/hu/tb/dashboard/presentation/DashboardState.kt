package hu.tb.dashboard.presentation

import androidx.compose.runtime.Immutable
import hu.tb.dashboard.domain.CoachItem
import hu.tb.dashboard.domain.FreeSession
import hu.tb.dashboard.domain.SessionItem
import hu.tb.datastore.ProfileType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Immutable
data class DashboardState(
    val profileType: ProfileType? = null,
    val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val selectedDate: LocalDate = today,
    val bookedSessions: List<SessionItem> = emptyList(),
    val freeSessions: List<FreeSession> = emptyList(),
    val isMyCoachesLoading: Boolean = true,
    val myCoaches: List<CoachItem>? = null
) {
    fun getBookedSessions(date: LocalDate): List<SessionItem> =
        bookedSessions.filter { it.date == date }.sortedBy { it.start }

    fun getFreeSessions(date: LocalDate): List<FreeSession> =
        freeSessions.filter { it.date == date }.sortedBy { it.start }

    fun coachNameOf(coachId: String): String? =
        myCoaches?.firstOrNull { it.id == coachId }?.name
}
