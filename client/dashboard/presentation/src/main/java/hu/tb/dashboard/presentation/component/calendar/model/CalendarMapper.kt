package hu.tb.dashboard.presentation.component.calendar.model

import hu.tb.dashboard.domain.OpenSlot
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
    openSlots: List<OpenSlot>
): CalendarMonth = buildMonthGrid(month, dayAt(sessions, openSlots))

internal fun buildCalendarWeek(
    anchor: LocalDate,
    sessions: List<SessionItem>,
    openSlots: List<OpenSlot>
): CalendarWeek = buildWeekGrid(anchor, dayAt(sessions, openSlots))

private fun dayAt(
    sessions: List<SessionItem>,
    openSlots: List<OpenSlot>
): (LocalDate) -> CalendarDay {
    val sessionCounts = sessions.groupingBy { it.date }.eachCount()
    val openSlotDates = openSlots.mapTo(mutableSetOf()) { it.date }
    return { date ->
        CalendarDay(
            date = date,
            sessionCount = sessionCounts[date] ?: 0,
            hasOpenSlot = date in openSlotDates
        )
    }
}
