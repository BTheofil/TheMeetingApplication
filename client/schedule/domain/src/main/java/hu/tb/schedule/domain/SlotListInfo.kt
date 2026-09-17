package hu.tb.schedule.domain

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class SlotListInfo(
    val date: LocalDate,
    val sessions: List<TimeRange.SessionTime>,
    val drafts: List<TimeRange.DraftSlot> = emptyList()
)
