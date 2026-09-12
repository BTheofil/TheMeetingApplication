package hu.tb.meet.install

import hu.tb.meet.di.appModule
import hu.tb.meet.di.notificationModule
import hu.tb.meet.security.JwtService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    val configAudience = environment.config.property("jwt.audience").getString()
    val configIssuer = environment.config.property("jwt.issuer").getString()
    val configSecret = environment.config.propertyOrNull("jwt.secret.meeting")?.getString()
        ?: if (developmentMode) {
            log.warn("jwt.secret.meeting is not set, falling back to the built-in debug secret")
            "debug_build"
        } else {
            error("jwt.secret.meeting is not set, pass JWT_SECRET_MEETING to the container")
        }

    install(Koin) {
        slf4jLogger()
        modules(
            module { single { JwtService(issuer = configIssuer, audience = configAudience, secret = configSecret) } },
            notificationModule(),
            appModule
        )
    }
}
