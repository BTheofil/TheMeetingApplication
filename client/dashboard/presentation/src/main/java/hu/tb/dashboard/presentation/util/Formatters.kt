package hu.tb.dashboard.presentation.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus

private val timeFormat = LocalTime.Format {
    hour()
    char(':')
    minute()
}

private val dayLabelFormat = LocalDate.Format {
    dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
    char(' ')
    day(Padding.NONE)
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
}

private val fullDayLabelFormat = LocalDate.Format {
    dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
    chars(", ")
    day(Padding.NONE)
    char(' ')
    monthName(MonthNames.ENGLISH_FULL)
}

internal fun LocalTime.formatTime(): String = format(timeFormat)

internal fun LocalDate.formatDayLabel(): String = format(dayLabelFormat)

internal fun LocalDate.formatSectionLabel(today: LocalDate): String = when (this) {
    today -> "Today"
    today.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
    today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
    else -> format(fullDayLabelFormat)
}
