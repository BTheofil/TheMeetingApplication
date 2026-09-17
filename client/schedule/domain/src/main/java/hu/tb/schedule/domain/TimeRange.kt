package hu.tb.schedule.domain

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalTime

sealed interface TimeRange {
    val start: LocalTime
    val end: LocalTime

    @Immutable
    data class DraftSlot(
        override val start: LocalTime,
        override val end: LocalTime
    ) : TimeRange

    @Immutable
    data class SessionTime(
        val id: Int,
        override val start: LocalTime,
        override val end: LocalTime
    ) : TimeRange
}

fun TimeRange.formattedTimeUi(): String = "${start.formattedTimeUi()} – ${end.formattedTimeUi()}"

fun TimeRange.toDraft(): TimeRange.DraftSlot = TimeRange.DraftSlot(start = start, end = end)
