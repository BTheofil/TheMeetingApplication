package hu.tb.dashboard.presentation.model

import hu.tb.dashboard.presentation.DashboardState
import hu.tb.dashboard.presentation.component.calendar.WeekDays
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.plus
import kotlinx.datetime.previousOrSame

internal const val WEEKS_IN_GRID = 6

internal fun DashboardState.buildCalendarMonth(month: YearMonth): CalendarMonth {
    val gridStart = month.firstDay.previousOrSame(WeekDays.first())
    val index = DayIndex(this)

    return CalendarMonth(
        weeks = List(WEEKS_IN_GRID) { week ->
            index.weekFrom(gridStart.plus(week * WeekDays.size, DateTimeUnit.DAY))
        }
    )
}

internal fun DashboardState.buildCalendarWeek(anchor: LocalDate): CalendarWeek =
    DayIndex(this).weekFrom(anchor.previousOrSame(WeekDays.first()))

private class DayIndex(state: DashboardState) {

    private val sessionCounts: Map<LocalDate, Int> =
        state.sessions.groupingBy { it.date }.eachCount()

    private val openSlotDates: Set<LocalDate> =
        state.openSlots.mapTo(mutableSetOf()) { it.date }

    fun weekFrom(firstDay: LocalDate): CalendarWeek =
        CalendarWeek(
            days = List(WeekDays.size) { dayIndex ->
                dayAt(firstDay.plus(dayIndex, DateTimeUnit.DAY))
            }
        )

    private fun dayAt(date: LocalDate): CalendarDay =
        CalendarDay(
            date = date,
            sessionCount = sessionCounts[date] ?: 0,
            hasOpenSlot = date in openSlotDates
        )
}
