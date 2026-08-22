package hu.tb.meet.di

import hu.tb.meet.domain.JwtConfig
import hu.tb.meet.security.JwtService
import org.koin.dsl.module

fun tokenModule(config: JwtConfig) = module {
    single { JwtService(config) }
}