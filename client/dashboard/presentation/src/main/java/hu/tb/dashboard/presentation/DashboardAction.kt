package hu.tb.dashboard.presentation

import kotlinx.datetime.LocalDate

sealed interface DashboardAction {
    data object OnProfileClick : DashboardAction
    data class OnDateSelect(val date: LocalDate) : DashboardAction
    data object OnPreviousMonth : DashboardAction
    data object OnNextMonth : DashboardAction
    data class OnSessionClick(val id: String) : DashboardAction
    data object OnCreateOpenHoursClick : DashboardAction
    data class OnCoachClick(val coachId: String) : DashboardAction
}
