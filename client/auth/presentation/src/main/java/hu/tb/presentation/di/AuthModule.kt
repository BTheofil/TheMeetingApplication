package hu.tb.presentation.di

import hu.tb.domain.AuthMode
import hu.tb.presentation.form.AuthFormViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { (mode: AuthMode) ->
        AuthFormViewModel(
            mode = mode,
            authRepository = get(),
            userDatastoreRepository = get()
        )
    }
}
