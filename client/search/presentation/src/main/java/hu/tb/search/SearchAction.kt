package hu.tb.search

interface NavigationRequest

sealed interface SearchAction {
    data object OnBackRequest : SearchAction, NavigationRequest
    data class OnSearch(val query: String) : SearchAction
    data class OnCoachAddRequest(val coachId: String) : SearchAction
}