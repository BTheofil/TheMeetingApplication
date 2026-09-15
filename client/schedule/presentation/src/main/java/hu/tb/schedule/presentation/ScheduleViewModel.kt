package hu.tb.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.network.repository.ScheduleRepository
import hu.tb.schedule.domain.DraftSlot
import hu.tb.schedule.domain.TimeSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            scheduleRepository.getMonth()
        }
    }

    fun action(action: ScheduleAction) {
        when (action) {
            ScheduleAction.BackRequest -> Unit
            ScheduleAction.PreviousMonth -> _state.update {
                it.copy(visibleMonth = it.visibleMonth.minusMonth())
            }

            ScheduleAction.NextMonth -> _state.update {
                it.copy(visibleMonth = it.visibleMonth.plusMonth())
            }

            is ScheduleAction.DateSelect -> _state.update {
                it.copy(selectedDate = action.date)
            }

            is ScheduleAction.DraftConfirm -> addDraft(action.date, action.draft)
            is ScheduleAction.SlotDelete -> deleteSlot(action.date, action.slot)
            is ScheduleAction.SlotCopy -> copySlot(action.slot)
            is ScheduleAction.DraftDelete -> deleteDraft(action.date, action.draft)
            is ScheduleAction.DayCopy -> copyDay(action.date)
            is ScheduleAction.DayPaste -> pasteDay(action.date)
            ScheduleAction.PublishDrafts -> TODO()
        }
    }

    private fun addDraft(date: LocalDate, draft: DraftSlot) {
        _state.update { state ->
            state.copy(
                drafts = state.drafts + (date to (state.drafts[date].orEmpty() + draft))
            )
        }
    }

    private fun deleteSlot(date: LocalDate, slot: TimeSlot) {
        _state.update { state ->
            val remaining = state.slotsByDate[date].orEmpty() - slot
            state.copy(
                slotsByDate = if (remaining.isEmpty()) state.slotsByDate - date
                else state.slotsByDate + (date to remaining)
            )
        }
    }

    private fun deleteDraft(date: LocalDate, draft: DraftSlot) {
        _state.update { state ->
            val remaining = state.drafts[date].orEmpty() - draft
            state.copy(
                drafts = if (remaining.isEmpty()) state.drafts - date
                else state.drafts + (date to remaining)
            )
        }
    }

    private fun copySlot(slot: DraftSlot) {
        _state.update { state ->
            state.copy(clipboard = listOf(slot))
        }
    }

    private fun copyDay(date: LocalDate) {
        _state.update { state ->
            state.copy(clipboard = state.dayTimes(date))
        }
    }

    private fun pasteDay(date: LocalDate) {
        _state.update { state ->
            val newDrafts = state.clipboard - state.dayTimes(date).toSet()
            if (newDrafts.isEmpty()) return@update state
            state.copy(
                drafts = state.drafts + (date to (state.drafts[date].orEmpty() + newDrafts)
                    .sortedBy { it.start })
            )
        }
    }

    private fun ScheduleState.dayTimes(date: LocalDate): List<DraftSlot> =
        (slotsByDate[date].orEmpty().map { it.toDraft() } + drafts[date].orEmpty())
            .sortedBy { it.start }
}
