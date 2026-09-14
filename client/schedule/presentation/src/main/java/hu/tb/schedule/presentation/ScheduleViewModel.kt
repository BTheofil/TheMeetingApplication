package hu.tb.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.network.repository.ScheduleRepository
import hu.tb.schedule.domain.DraftSlot
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

            is ScheduleAction.DraftConfirm -> createSlot(action.date, action.draft)
        }
    }

    private fun createSlot(date: LocalDate, draft: DraftSlot) {

    }
}
