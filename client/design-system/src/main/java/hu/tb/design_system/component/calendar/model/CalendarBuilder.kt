package hu.tb.design_system.component.calendar.model

import hu.tb.design_system.component.calendar.WeekDays
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.plus
import kotlinx.datetime.previousOrSame

private const val WEEKS_IN_GRID = 6

fun buildCalendarMonth(
    month: YearMonth,
    dayAt: (LocalDate) -> CalendarDay = ::CalendarDay
): CalendarMonth {
    val gridStart = month.firstDay.previousOrSame(WeekDays.first())
    return CalendarMonth(
        weeks = List(WEEKS_IN_GRID) { week ->
            weekFrom(
                firstDay = gridStart.plus(week * WeekDays.size, DateTimeUnit.DAY),
                dayAt = dayAt
            )
        }
    )
}

fun buildCalendarWeek(
    anchor: LocalDate,
    dayAt: (LocalDate) -> CalendarDay = ::CalendarDay
): CalendarWeek =
    weekFrom(firstDay = anchor.previousOrSame(WeekDays.first()), dayAt = dayAt)

private fun weekFrom(
    firstDay: LocalDate,
    dayAt: (LocalDate) -> CalendarDay
): CalendarWeek =
    CalendarWeek(
        days = List(WeekDays.size) { dayIndex ->
            dayAt(firstDay.plus(dayIndex, DateTimeUnit.DAY))
        }
    )
