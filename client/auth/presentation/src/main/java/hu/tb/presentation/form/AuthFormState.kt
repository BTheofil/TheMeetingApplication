package hu.tb.presentation.form

import hu.tb.domain.AuthMode

data class AuthFormState(
    val mode: AuthMode = AuthMode.REGISTER,
    val isLoading: Boolean = false
)
