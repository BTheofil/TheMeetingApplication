package hu.tb.profile.presentation

sealed interface ProfileEvent {
    data object Deleted : ProfileEvent
    data class Failed(val errorMessage: String) : ProfileEvent
}
