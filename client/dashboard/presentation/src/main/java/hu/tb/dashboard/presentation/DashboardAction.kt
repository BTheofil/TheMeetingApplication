package hu.tb.dashboard.presentation

import kotlinx.datetime.LocalDate

interface NavigationRequest

sealed interface DashboardAction {
    data object OnProfileClick : DashboardAction, NavigationRequest
    data class OnDateSelect(val date: LocalDate) : DashboardAction
    data class OnSessionClick(val id: String) : DashboardAction, NavigationRequest
    data object OnCreateOpenHoursClick : DashboardAction, NavigationRequest
    data class OnCoachClick(val coachId: String) : DashboardAction, NavigationRequest
    data object OnDiscoverCoachesClick : DashboardAction, NavigationRequest
}
