package hu.tb.dashboard.presentation.model

import hu.tb.dashboard.presentation.component.calendar.WeekDays
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.plus
import kotlinx.datetime.previousOrSame

internal const val WEEKS_IN_GRID = 6

internal fun buildCalendarMonth(
    month: YearMonth,
    sessions: List<SessionItem>,
    openSlots: List<OpenSlot>
): CalendarMonth {
    val gridStart = month.firstDay.previousOrSame(WeekDays.first())
    return CalendarMonth(
        weeks = List(WEEKS_IN_GRID) { week ->
            DayIndex().weekFrom(
                firstDay = gridStart.plus(week * WeekDays.size, DateTimeUnit.DAY),
                sessions = sessions,
                openSlots = openSlots
            )
        }
    )
}

internal fun buildCalendarWeek(
    anchor: LocalDate,
    sessions: List<SessionItem>,
    openSlots: List<OpenSlot>
): CalendarWeek =
    DayIndex().weekFrom(
        firstDay = anchor.previousOrSame(WeekDays.first()),
        sessions = sessions,
        openSlots = openSlots
    )

private class DayIndex() {
    fun weekFrom(
        firstDay: LocalDate,
        sessions: List<SessionItem>,
        openSlots: List<OpenSlot>
    ): CalendarWeek =
        CalendarWeek(
            days = List(WeekDays.size) { dayIndex ->
                dayAt(
                    date = firstDay.plus(dayIndex, DateTimeUnit.DAY),
                    sessions = sessions.groupingBy { it.date }.eachCount(),
                    openSlots = openSlots.mapTo(mutableSetOf()) { it.date }
                )
            }
        )

    private fun dayAt(
        date: LocalDate,
        sessions: Map<LocalDate, Int>,
        openSlots: Set<LocalDate>
    ): CalendarDay =
        CalendarDay(
            date = date,
            sessionCount = sessions[date] ?: 0,
            hasOpenSlot = date in openSlots
        )
}
