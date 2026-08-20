package hu.tb.presentation.di

import hu.tb.presentation.form.AuthFormViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    viewModelOf(::AuthFormViewModel)
}