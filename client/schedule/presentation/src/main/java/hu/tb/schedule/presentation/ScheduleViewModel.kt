package hu.tb.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.network.fold
import hu.tb.network.repository.ScheduleRepository
import hu.tb.schedule.domain.TimeRange
import hu.tb.schedule.domain.overlaps
import hu.tb.schedule.domain.toDraft
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    private val _event = Channel<String>()
    val event = _event.receiveAsFlow()

    init {
        loadMonth(state.value.visibleMonth)
    }

    fun action(action: ScheduleAction) {
        when (action) {
            ScheduleAction.BackRequest -> Unit
            ScheduleAction.PreviousMonth -> showMonth(state.value.visibleMonth.minusMonth())
            ScheduleAction.NextMonth -> showMonth(state.value.visibleMonth.plusMonth())

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

    private fun showMonth(month: YearMonth) {
        _state.update { it.copy(visibleMonth = month) }
        loadMonth(month)
    }

    private fun loadMonth(month: YearMonth) {
        if (month in state.value.loadedMonths) return

        viewModelScope.launch {
            _state.update { it.copy(isCalendarLoading = true) }
            scheduleRepository.getPublishedSessions(month.firstDay).fold(
                success = { sessions ->
                    _state.update {
                        it.copy(
                            sessionsByDate = it.sessionsByDate + sessions,
                            loadedMonths = it.loadedMonths +
                                    listOf(month.minusMonth(), month, month.plusMonth()),
                            isCalendarLoading = false
                        )
                    }
                },
                fail = { failure ->
                    _state.update { it.copy(isCalendarLoading = false) }
                    _event.send(failure.formatErrorMessage)
                }
            )
        }
    }

    private fun publishDrafts() {
        val drafts = state.value.draftsByDate
        if (drafts.isEmpty() || state.value.isPublishing) return

        viewModelScope.launch {
            _state.update { it.copy(isPublishing = true) }
            scheduleRepository.uploadDrafts(drafts).fold(
                success = {
                    _state.update {
                        it.copy(
                            draftsByDate = emptyMap(),
                            loadedMonths = emptySet(),
                            isPublishing = false
                        )
                    }
                    loadMonth(state.value.visibleMonth)
                },
                fail = { failure ->
                    _state.update { it.copy(isPublishing = false) }
                    _event.send(failure.formatErrorMessage)
                }
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
        if (session.id in state.value.deletingSessionIds) return

        viewModelScope.launch {
            _state.update { it.copy(deletingSessionIds = it.deletingSessionIds + session.id) }
            scheduleRepository.deletePublishedSession(date, session.start, session.end).fold(
                success = {
                    _state.update { state ->
                        val remaining = state.sessionsByDate[date].orEmpty() - session
                        state.copy(
                            sessionsByDate = if (remaining.isEmpty()) state.sessionsByDate - date
                            else state.sessionsByDate + (date to remaining),
                            deletingSessionIds = state.deletingSessionIds - session.id
                        )
                    }
                },
                fail = { failure ->
                    _state.update { it.copy(deletingSessionIds = it.deletingSessionIds - session.id) }
                    _event.send(failure.formatErrorMessage)
                }
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
        val dayDrafts = state.value.dayDrafts(date)
        val newDrafts = slots
            .map { it.toDraft() }
            .filter { candidate -> dayDrafts.none { it.overlaps(candidate) } }
        val skippedCount = slots.size - newDrafts.size

        if (newDrafts.isNotEmpty()) {
            _state.update { state ->
                state.copy(
                    draftsByDate = state.draftsByDate + (date to (state.draftsByDate[date].orEmpty() + newDrafts)
                        .sortedBy { it.start })
                )
            }
        }

        if (skippedCount > 0) {
            viewModelScope.launch {
                _event.send("$skippedCount overlapping time slot(s) skipped")
            }
        }
    }

    private fun ScheduleState.dayDrafts(date: LocalDate): List<TimeRange.DraftSlot> =
        (sessionsByDate[date].orEmpty().map { it.toDraft() } + draftsByDate[date].orEmpty())
            .sortedBy { it.start }
}
