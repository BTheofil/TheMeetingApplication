package hu.tb.presentation.di

import hu.tb.domain.AuthMode
import hu.tb.presentation.form.AuthFormViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    // Spelled out rather than viewModelOf(::AuthFormViewModel): the constructor reference DSL
    // resolves every argument from the container, which cannot supply the navigation `mode`.
    viewModel { (mode: AuthMode) ->
        AuthFormViewModel(
            mode = mode,
            authRepository = get(),
            userDatastoreRepository = get()
        )
    }
}
