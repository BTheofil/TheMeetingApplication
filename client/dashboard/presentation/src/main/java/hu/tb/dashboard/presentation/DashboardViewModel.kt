package hu.tb.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.datastore.ProfileType
import hu.tb.network.fold
import hu.tb.network.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class DashboardViewModel(
    private val userDatastoreRepository: UserDatastoreRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userDatastoreRepository.userdataFlow().collect { userData ->
                _state.update {
                    it.copy(profileType = ProfileType.fromValue(userData.profileType))
                }
            }
        }
        viewModelScope.launch {
            _state.update { it.copy(isMyCoachesLoading = true) }
            dashboardRepository.getCoaches().fold(
                success = { coaches ->
                    _state.update {
                        it.copy(
                            myCoaches = coaches,
                            isMyCoachesLoading = false
                        )
                    }
                },
                fail = {
                    _state.update { it.copy(isMyCoachesLoading = false) }
                }
            )
        }
    }

    fun onDateSelected(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
    }
}
