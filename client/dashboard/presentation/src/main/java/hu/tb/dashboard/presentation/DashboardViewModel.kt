package hu.tb.dashboard.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    fun onDateSelected(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
    }
}
