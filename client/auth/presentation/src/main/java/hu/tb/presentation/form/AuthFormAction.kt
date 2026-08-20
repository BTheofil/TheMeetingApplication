package hu.tb.presentation.form

import hu.tb.domain.LoginForm
import hu.tb.domain.RegisterForm

sealed interface AuthFormAction {
    data class OnRegister(val form: RegisterForm): AuthFormAction
    data class OnLogin(val form: LoginForm): AuthFormAction
}