package hu.tb.schedule.domain

import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char

fun LocalTime.formattedTimeUi(): String = format(LocalTime.Format {
    hour()
    char(':')
    minute()
})
