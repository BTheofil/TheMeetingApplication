package hu.tb.profile.presentation

sealed interface ProfileEvent {
    data object Cleared : ProfileEvent
    data class Failed(val errorMessage: String) : ProfileEvent
}
