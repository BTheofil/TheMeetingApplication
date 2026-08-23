package hu.tb.presentation.form

import hu.tb.domain.AuthForm
import hu.tb.domain.AuthMode

sealed interface AuthFormAction {
    data class OnSubmit(val authMode: AuthMode, val form: AuthForm) : AuthFormAction
}