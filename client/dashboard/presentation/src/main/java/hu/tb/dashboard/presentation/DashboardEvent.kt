package hu.tb.dashboard.presentation

sealed interface DashboardEvent {
    data class Failed(val errorMessage: String) : DashboardEvent
}
