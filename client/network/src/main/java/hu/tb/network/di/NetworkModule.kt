package hu.tb.network.di

import hu.tb.network.TokenProvider
import hu.tb.network.repository.AuthRepository
import hu.tb.network.repository.ProfileRepository
import hu.tb.network.repository.SearchRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        val tokenProvider = get<TokenProvider>()

        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = true
                        prettyPrint = true
                    }
                )
            }
            install(Auth) {
                bearer {
                    cacheTokens = false
                    loadTokens {
                        tokenProvider.token()?.let { BearerTokens(it, null) }
                    }
                }
            }
            install(Logging) {
                level = LogLevel.HEADERS
            }
            defaultRequest {
                url("https://theohome-meeting.duckdns.org")
                contentType(ContentType.Application.Json)
            }
        }
    }

    singleOf(::AuthRepository)
    singleOf(::ProfileRepository)
    singleOf(::SearchRepository)
}
