package hu.tb.presentation.form

import hu.tb.network.DataError

sealed interface AuthFormEvent {
    data object Success : AuthFormEvent
    data class Failed(val error: DataError) : AuthFormEvent
}