package hu.tb.dashboard.presentation.component.calendar.model

import hu.tb.dashboard.domain.FreeSession
import hu.tb.dashboard.domain.SessionItem
import hu.tb.design_system.component.calendar.model.CalendarDay
import hu.tb.design_system.component.calendar.model.CalendarMonth
import hu.tb.design_system.component.calendar.model.CalendarWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import hu.tb.design_system.component.calendar.model.buildCalendarMonth as buildMonthGrid
import hu.tb.design_system.component.calendar.model.buildCalendarWeek as buildWeekGrid

internal fun buildCalendarMonth(
    month: YearMonth,
    sessions: List<SessionItem>,
    freeSessions: List<FreeSession>
): CalendarMonth = buildMonthGrid(month, dayAt(sessions, freeSessions))

internal fun buildCalendarWeek(
    anchor: LocalDate,
    sessions: List<SessionItem>,
    freeSessions: List<FreeSession>
): CalendarWeek = buildWeekGrid(anchor, dayAt(sessions, freeSessions))

private fun dayAt(
    sessions: List<SessionItem>,
    freeSessions: List<FreeSession>
): (LocalDate) -> CalendarDay {
    val sessionCounts = sessions.groupingBy { it.date }.eachCount()
    val openSlotDates = freeSessions.mapTo(mutableSetOf()) { it.date }
    return { date ->
        CalendarDay(
            date = date,
            sessionCount = sessionCounts[date] ?: 0,
            hasOpenSlot = date in openSlotDates
        )
    }
}
