package hu.tb.profile.presentation

sealed interface ProfileEvent {
    data object ProfileCleared : ProfileEvent
    data object ShowThankYouDialog : ProfileEvent
    data object ShowDeleteLoadingDialog : ProfileEvent
    data class Failed(val errorMessage: String) : ProfileEvent
}
