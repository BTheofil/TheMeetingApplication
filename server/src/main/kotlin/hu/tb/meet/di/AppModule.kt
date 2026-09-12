package hu.tb.meet.di

import hu.tb.meet.data.repository.AuthRepository
import hu.tb.meet.data.repository.DeviceFidRepository
import hu.tb.meet.data.repository.ProfileRepository
import hu.tb.meet.data.repository.SearchRepository
import hu.tb.meet.data.repository.RequestRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AuthRepository)
    singleOf(::ProfileRepository)
    singleOf(::SearchRepository)
    singleOf(::RequestRepository)
    singleOf(::DeviceFidRepository)
}
