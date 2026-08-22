package hu.tb.meet.di

import hu.tb.meet.data.repository.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AuthRepository)
}
