package hu.tb.dashboard.presentation.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

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

private val monthLabelFormat = YearMonth.Format {
    monthName(MonthNames.ENGLISH_FULL)
    char(' ')
    year()
}

internal fun currentDate(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

internal fun LocalTime.formatTime(): String = format(timeFormat)

internal fun LocalDate.formatDayLabel(): String = format(dayLabelFormat)

internal fun YearMonth.formatMonthLabel(): String = format(monthLabelFormat)

internal fun LocalDate.formatSectionLabel(today: LocalDate): String = when (this) {
    today -> "Today"
    today.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
    today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
    else -> format(fullDayLabelFormat)
}

internal fun DayOfWeek.narrowLabel(): String =
    DayOfWeekNames.ENGLISH_ABBREVIATED.names[isoDayNumber - 1].take(1).uppercase()
