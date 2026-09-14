package hu.tb.design_system.component.calendar

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.isoDayNumber

private val monthLabelFormat = YearMonth.Format {
    monthName(MonthNames.ENGLISH_FULL)
    char(' ')
    year()
}

fun YearMonth.formatMonthLabel(): String = format(monthLabelFormat)

fun DayOfWeek.narrowLabel(): String =
    DayOfWeekNames.ENGLISH_ABBREVIATED.names[isoDayNumber - 1].take(1).uppercase()
