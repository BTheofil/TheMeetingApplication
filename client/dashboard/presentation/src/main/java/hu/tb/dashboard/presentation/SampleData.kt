package hu.tb.dashboard.presentation

import hu.tb.dashboard.presentation.model.CalendarDay
import hu.tb.dashboard.presentation.model.CoachItem
import hu.tb.dashboard.presentation.model.OpenSlot
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.domain.ProfileType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import kotlinx.datetime.yearMonth

internal object SampleData {

    private val today: LocalDate get() = currentDate()

    private val coachNames = mapOf(
        "coach-anna" to "Anna Kovács",
        "coach-mark" to "Márk Szabó",
        "coach-julia" to "Júlia Papp"
    )

    fun clientSessions(): List<SessionItem> {
        val base = today
        return listOf(
            SessionItem(
                id = "s1",
                title = "Leg day",
                counterpartName = "Anna Kovács",
                date = base,
                start = LocalTime(9, 0),
                durationMinutes = 60,
                isNext = true
            ),
            SessionItem(
                id = "s2",
                title = "Mobility",
                counterpartName = "Júlia Papp",
                date = base,
                start = LocalTime(17, 30),
                durationMinutes = 45
            ),
            SessionItem(
                id = "s3",
                title = "Cardio intervals",
                counterpartName = "Márk Szabó",
                date = base.plus(1, DateTimeUnit.DAY),
                start = LocalTime(18, 30),
                durationMinutes = 45
            ),
            SessionItem(
                id = "s4",
                title = "Upper body",
                counterpartName = "Anna Kovács",
                date = base.plus(4, DateTimeUnit.DAY),
                start = LocalTime(8, 0),
                durationMinutes = 60
            ),
            SessionItem(
                id = "s5",
                title = "Progress check",
                counterpartName = "Júlia Papp",
                date = base.plus(9, DateTimeUnit.DAY),
                start = LocalTime(11, 0),
                durationMinutes = 30
            )
        )
    }

    fun coachSessions(): List<SessionItem> {
        val base = today
        return listOf(
            SessionItem(
                id = "c1",
                title = "Leg day",
                counterpartName = "Theo B.",
                date = base,
                start = LocalTime(9, 0),
                durationMinutes = 60,
                isNext = true
            ),
            SessionItem(
                id = "c2",
                title = "Intro session",
                counterpartName = "Márk S.",
                date = base,
                start = LocalTime(11, 0),
                durationMinutes = 30
            ),
            SessionItem(
                id = "c3",
                title = "Mobility",
                counterpartName = "Júlia P.",
                date = base,
                start = LocalTime(17, 30),
                durationMinutes = 45
            ),
            SessionItem(
                id = "c4",
                title = "Cardio intervals",
                counterpartName = "Theo B.",
                date = base.plus(1, DateTimeUnit.DAY),
                start = LocalTime(18, 30),
                durationMinutes = 45
            ),
            SessionItem(
                id = "c5",
                title = "Strength block",
                counterpartName = "Márk S.",
                date = base.plus(3, DateTimeUnit.DAY),
                start = LocalTime(7, 30),
                durationMinutes = 60
            ),
            SessionItem(
                id = "c6",
                title = "Progress check",
                counterpartName = "Júlia P.",
                date = base.plus(9, DateTimeUnit.DAY),
                start = LocalTime(11, 0),
                durationMinutes = 30
            )
        )
    }

    fun openSlots(): List<OpenSlot> {
        val base = today
        return listOf(
            OpenSlot("o0", "coach-anna", base, LocalTime(15, 0), 45),
            OpenSlot("o1", "coach-anna", base.plus(2, DateTimeUnit.DAY), LocalTime(10, 0), 60),
            OpenSlot("o2", "coach-anna", base.plus(2, DateTimeUnit.DAY), LocalTime(14, 0), 45),
            OpenSlot("o3", "coach-anna", base.plus(5, DateTimeUnit.DAY), LocalTime(8, 30), 60),
            OpenSlot("o4", "coach-mark", base.plus(6, DateTimeUnit.DAY), LocalTime(16, 0), 30),
            OpenSlot("o5", "coach-mark", base.plus(11, DateTimeUnit.DAY), LocalTime(9, 15), 60),
            OpenSlot("o6", "coach-mark", base.plus(11, DateTimeUnit.DAY), LocalTime(13, 0), 45),
            OpenSlot("o7", "coach-mark", base.plus(12, DateTimeUnit.DAY), LocalTime(7, 45), 60)
        )
    }

    fun coaches(): List<CoachItem> {
        val slotsByCoach = openSlots().groupingBy { it.coachId }.eachCount()
        return coachNames.map { (id, name) ->
            CoachItem(
                id = id,
                name = name,
                openHourCount = slotsByCoach[id] ?: 0
            )
        }
    }

    fun singleCoach(): CoachItem = coaches().first()

    fun nextSession(): SessionItem = clientSessions().first()

    fun laterSession(): SessionItem = clientSessions()[3]

    fun clientState(): DashboardState = stateOf(
        profileType = ProfileType.NORMAL,
        sessions = clientSessions(),
        slots = openSlots(),
        coaches = coaches()
    )

    fun clientStateNoCoaches(): DashboardState = stateOf(
        profileType = ProfileType.NORMAL,
        sessions = emptyList(),
        slots = emptyList(),
        coaches = emptyList()
    )

    fun coachState(): DashboardState = stateOf(
        profileType = ProfileType.COACH,
        sessions = coachSessions(),
        slots = openSlots(),
        coaches = emptyList()
    )

    fun stateOf(
        profileType: ProfileType,
        sessions: List<SessionItem>,
        slots: List<OpenSlot>,
        coaches: List<CoachItem>,
        selectedDate: LocalDate = currentDate()
    ): DashboardState {
        val base = currentDate()
        return DashboardState(
            profileType = profileType,
            today = base,
            selectedDate = selectedDate,
            visibleMonth = selectedDate.yearMonth,
            days = buildDays(sessions, slots),
            sessionsOnSelectedDay = sessions
                .filter { it.date == selectedDate }
                .sortedBy { it.start },
            openSlotsOnSelectedDay = slots
                .filter { it.date == selectedDate }
                .sortedBy { it.start },
            coaches = coaches,
            availableSlots = slots
        )
    }

    private fun buildDays(
        sessions: List<SessionItem>,
        slots: List<OpenSlot>
    ): List<CalendarDay> {
        val dates = (sessions.map { it.date } + slots.map { it.date }).distinct()
        return dates.map { date ->
            CalendarDay(
                date = date,
                sessionCount = sessions.count { it.date == date },
                hasOpenSlot = slots.any { it.date == date }
            )
        }
    }
}
