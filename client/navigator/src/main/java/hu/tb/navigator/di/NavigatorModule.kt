package hu.tb.navigator.di

import hu.tb.navigator.NavigatorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val navigatorModule = module {
    viewModelOf(::NavigatorViewModel)
}
