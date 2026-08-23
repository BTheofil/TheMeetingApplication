package hu.tb.network.di

import hu.tb.network.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = true
                        prettyPrint = true
                    }
                )
            }
            install(Logging) {}
            defaultRequest {
                url("https://theohome-meeting.duckdns.org")
                contentType(ContentType.Application.Json)
            }
        }
    }

    singleOf(::AuthRepository)
}
