package hu.tb.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.network.fold
import hu.tb.network.repository.ScheduleRepository
import hu.tb.schedule.domain.TimeRange
import hu.tb.schedule.domain.toDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth
import kotlinx.datetime.todayIn
import kotlin.collections.toSet
import kotlin.time.Clock

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    init {
        getPublishedSessions()
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
            is ScheduleAction.SessionDelete -> deleteSession(action.date, action.session)
            is ScheduleAction.DraftDelete -> deleteDraft(action.date, action.draft)
            is ScheduleAction.DayPaste -> pasteDay(action.date, action.slots)
            ScheduleAction.DraftsPublish -> publishDrafts()
        }
    }

    private fun publishDrafts() {
        val drafts = state.value.draftsByDate
        if (drafts.isEmpty()) return

        viewModelScope.launch {
            scheduleRepository.uploadDrafts(drafts).fold(
                success = {
                    _state.update { it.copy(draftsByDate = emptyMap()) }
                    getPublishedSessions()
                },
                fail = {}
            )
        }
    }

    private fun getPublishedSessions() {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            scheduleRepository.getPublishedSessions(today).fold(
                success = { response ->
                    _state.update { it.copy(sessionsByDate = response) }
                },
                fail = {}
            )
        }
    }

    private fun addDraft(date: LocalDate, draft: TimeRange.DraftSlot) {
        _state.update { state ->
            state.copy(
                draftsByDate = state.draftsByDate + (date to (state.draftsByDate[date].orEmpty() + draft))
            )
        }
    }

    private fun deleteSession(date: LocalDate, session: TimeRange.SessionTime) {
        viewModelScope.launch {
            scheduleRepository.deletePublishedSession(date, session.start, session.end).fold(
                success = {
                    _state.update { state ->
                        val remaining = state.sessionsByDate[date].orEmpty() - session
                        state.copy(
                            sessionsByDate = if (remaining.isEmpty()) state.sessionsByDate - date
                            else state.sessionsByDate + (date to remaining)
                        )
                    }
                },
                fail = {}
            )
        }
    }

    private fun deleteDraft(date: LocalDate, draft: TimeRange.DraftSlot) {
        _state.update { state ->
            val remaining = state.draftsByDate[date].orEmpty() - draft
            state.copy(
                draftsByDate = if (remaining.isEmpty()) state.draftsByDate - date
                else state.draftsByDate + (date to remaining)
            )
        }
    }

    private fun pasteDay(date: LocalDate, slots: List<TimeRange>) {
        _state.update { state ->
            val newDrafts = slots.map { it.toDraft() } - state.dayDrafts(date).toSet()
            if (newDrafts.isEmpty()) return@update state
            state.copy(
                draftsByDate = state.draftsByDate + (date to (state.draftsByDate[date].orEmpty() + newDrafts)
                    .sortedBy { it.start })
            )
        }
    }

    private fun ScheduleState.dayDrafts(date: LocalDate): List<TimeRange.DraftSlot> =
        (sessionsByDate[date].orEmpty().map { it.toDraft() } + draftsByDate[date].orEmpty())
            .sortedBy { it.start }
}
