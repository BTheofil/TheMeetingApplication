package hu.tb.schedule.presentation

import hu.tb.schedule.domain.TimeRange
import kotlinx.datetime.LocalDate

sealed interface ScheduleAction {
    data object BackRequest : ScheduleAction
    data object PreviousMonth : ScheduleAction
    data object NextMonth : ScheduleAction
    data object DraftsPublish : ScheduleAction
    data class DateSelect(val date: LocalDate) : ScheduleAction
    data class DraftConfirm(val date: LocalDate, val draft: TimeRange.DraftSlot) : ScheduleAction
    data class SessionDelete(val date: LocalDate, val session: TimeRange.SessionTime) : ScheduleAction
    data class DraftDelete(val date: LocalDate, val draft: TimeRange.DraftSlot) : ScheduleAction
    data class DayPaste(val date: LocalDate, val slots: List<TimeRange>) : ScheduleAction
}
