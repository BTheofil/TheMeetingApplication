package hu.tb.profile.presentation

sealed interface ProfileAction {
    data object OnBackClick : ProfileAction
    data object OnDeleteConfirmed : ProfileAction
}
