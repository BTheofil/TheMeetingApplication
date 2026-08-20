package hu.tb.presentation.form

sealed interface AuthFormEvent {
    data object Success: AuthFormEvent
    data class Failed(val cause: String): AuthFormEvent
}