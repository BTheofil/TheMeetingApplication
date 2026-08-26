package hu.tb.navigator

sealed interface SessionState {
    data object Init : SessionState
    data object NoUserSavedData : SessionState
    data object LoggedIn : SessionState
    data object Expired : SessionState
}