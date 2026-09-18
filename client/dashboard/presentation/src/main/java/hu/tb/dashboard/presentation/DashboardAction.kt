package hu.tb.dashboard.presentation

import kotlinx.datetime.LocalDate

interface NavigationRequest

sealed interface DashboardAction {
    data object OnProfileClick : DashboardAction, NavigationRequest
    data object OnCreateOpenHoursClick : DashboardAction, NavigationRequest
    data class ShowCoachFreeSessions(val coachId: String) : DashboardAction, NavigationRequest
    data object OnDiscoverCoachesClick : DashboardAction, NavigationRequest
    data object OnNotificationClick : DashboardAction, NavigationRequest
    data class OnDateSelect(val date: LocalDate) : DashboardAction
}
