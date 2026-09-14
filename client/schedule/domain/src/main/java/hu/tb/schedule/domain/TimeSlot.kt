package hu.tb.schedule.domain

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char

@Immutable
data class TimeSlot(
    val id: Long,
    val start: LocalTime,
    val end: LocalTime
) {
    fun formattedTimeUi(): String = "${start.formatTime()} – ${end.formatTime()}"

    private fun LocalTime.formatTime(): String = format(LocalTime.Format {
        hour()
        char(':')
        minute()
    })
}