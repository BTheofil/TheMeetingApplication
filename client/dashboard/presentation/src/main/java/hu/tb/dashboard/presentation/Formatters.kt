package hu.tb.dashboard.presentation

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

internal fun LocalTime.formatTime(): String = format(timeFormatter)

internal fun LocalDate.formatDayLabel(): String {
    val locale = Locale.getDefault()
    val weekday = dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
    val month = month.getDisplayName(TextStyle.SHORT, locale)
    return "$weekday $dayOfMonth $month"
}

internal fun LocalDate.formatSectionLabel(today: LocalDate): String {
    val locale = Locale.getDefault()
    return when (this) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        today.minusDays(1) -> "Yesterday"
        else -> {
            val weekday = dayOfWeek.getDisplayName(TextStyle.FULL, locale)
            val month = month.getDisplayName(TextStyle.FULL, locale)
            "$weekday, $dayOfMonth $month"
        }
    }
}
