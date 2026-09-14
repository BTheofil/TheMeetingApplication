package hu.tb.schedule.presentation

import hu.tb.schedule.domain.DraftSlot
import kotlinx.datetime.LocalDate

sealed interface ScheduleAction {
    data object BackRequest : ScheduleAction
    data object PreviousMonth : ScheduleAction
    data object NextMonth : ScheduleAction
    data class DateSelect(val date: LocalDate) : ScheduleAction
    data class DraftConfirm(val date: LocalDate, val draft: DraftSlot) : ScheduleAction
}
