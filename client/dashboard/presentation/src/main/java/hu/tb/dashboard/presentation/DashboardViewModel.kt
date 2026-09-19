package hu.tb.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.dashboard.domain.FreeSession
import hu.tb.dashboard.domain.SessionItem
import hu.tb.datastore.ProfileType
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.network.fold
import hu.tb.network.repository.DashboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class DashboardViewModel(
    private val userDatastoreRepository: UserDatastoreRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _event = Channel<DashboardEvent>()
    val event = _event.receiveAsFlow()

    private var loadedMonth: LocalDate? = null
    private var sessionsJob: Job? = null
    private var isBooking = false
    private var lastError: String? = null

    fun refresh() {
        loadedMonth = null
        loadProfile()
    }

    fun onDateSelected(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
        if (state.value.profileType == ProfileType.COACH) loadCoachSessions(date)
        else loadFreeSessions(date)
    }

    fun bookSession(freeSession: FreeSession) {
        if (isBooking) return
        isBooking = true

        viewModelScope.launch {
            dashboardRepository.bookASession(freeSession.id).fold(
                success = {
                    loadedMonth = null
                    loadFreeSessions(state.value.selectedDate)
                    loadBookedSessions()
                },
                fail = { notifyFailure(it.formatErrorMessage) }
            )
            isBooking = false
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val profileType = ProfileType.fromValue(userDatastoreRepository.userdataFlow().first().profileType)
            _state.update { it.copy(profileType = profileType) }

            if (profileType == ProfileType.COACH) {
                loadCoachSessions(state.value.selectedDate)
                return@launch
            }
            if (profileType != ProfileType.NORMAL) return@launch

            _state.update { it.copy(isMyCoachesLoading = it.myCoaches == null) }
            dashboardRepository.getCoaches().fold(
                success = { coaches ->
                    _state.update { it.copy(myCoaches = coaches, isMyCoachesLoading = false) }
                    loadFreeSessions(state.value.selectedDate)
                },
                fail = {
                    _state.update { it.copy(isMyCoachesLoading = false) }
                    notifyFailure(it.formatErrorMessage)
                }
            )
            loadBookedSessions()
        }
    }

    private fun loadFreeSessions(date: LocalDate) {
        val coaches = state.value.myCoaches.orEmpty()
        val month = LocalDate(date.year, date.month, 1)
        if (coaches.isEmpty() || month == loadedMonth) return

        sessionsJob?.cancel()
        sessionsJob = viewModelScope.launch {
            var errorMessage: String? = null
            val sessions = coaches
                .map { coach -> async { dashboardRepository.getFreeSessions(coach.id, month) } }
                .awaitAll()
                .flatMap { result ->
                    result.fold(
                        success = { it },
                        fail = { errorMessage = it.formatErrorMessage; emptyList() }
                    )
                }

            if (errorMessage == null) loadedMonth = month
            _state.update { it.copy(freeSessions = sessions) }
            errorMessage?.let { notifyFailure(it) }
        }
    }

    private fun loadCoachSessions(date: LocalDate) {
        val month = LocalDate(date.year, date.month, 1)
        if (month == loadedMonth) return

        sessionsJob?.cancel()
        sessionsJob = viewModelScope.launch {
            dashboardRepository.getCoachSessions(month).fold(
                success = { sessions ->
                    loadedMonth = month
                    lastError = null
                    _state.update { it.copy(bookedSessions = sessions.markNext()) }
                },
                fail = { notifyFailure(it.formatErrorMessage) }
            )
        }
    }

    private fun loadBookedSessions() {
        viewModelScope.launch {
            dashboardRepository.getBookedSessions().fold(
                success = { sessions ->
                    lastError = null
                    _state.update { it.copy(bookedSessions = sessions.markNext()) }
                },
                fail = { notifyFailure(it.formatErrorMessage) }
            )
        }
    }

    private suspend fun notifyFailure(message: String) {
        if (message == lastError) return

        lastError = message
        _event.send(DashboardEvent.Failed(message))
    }

    private fun List<SessionItem>.markNext(): List<SessionItem> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val nextId = sortedWith(compareBy({ it.date }, { it.start }))
            .firstOrNull { it.date > now.date || (it.date == now.date && it.end > now.time) }
            ?.id
        return map { it.copy(isNext = it.id == nextId) }
    }
}
