package hu.tb.schedule.domain

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalTime

@Immutable
data class TimeSlot(
    val id: Long,
    val start: LocalTime,
    val end: LocalTime
) {
    fun formattedTimeUi(): String = "${start.formattedTimeUi()} – ${end.formattedTimeUi()}"

    fun toDraft(): DraftSlot = DraftSlot(start = start, end = end)
}
