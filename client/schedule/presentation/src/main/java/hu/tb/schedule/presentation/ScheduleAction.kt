package hu.tb.schedule.presentation

import hu.tb.schedule.domain.DraftSlot
import hu.tb.schedule.domain.TimeSlot
import kotlinx.datetime.LocalDate

sealed interface ScheduleAction {
    data object BackRequest : ScheduleAction
    data object PreviousMonth : ScheduleAction
    data object NextMonth : ScheduleAction
    data object PublishDrafts : ScheduleAction
    data class DateSelect(val date: LocalDate) : ScheduleAction
    data class DraftConfirm(val date: LocalDate, val draft: DraftSlot) : ScheduleAction
    data class SlotDelete(val date: LocalDate, val slot: TimeSlot) : ScheduleAction
    data class SlotCopy(val slot: DraftSlot) : ScheduleAction
    data class DraftDelete(val date: LocalDate, val draft: DraftSlot) : ScheduleAction
    data class DayCopy(val date: LocalDate) : ScheduleAction
    data class DayPaste(val date: LocalDate) : ScheduleAction
}
