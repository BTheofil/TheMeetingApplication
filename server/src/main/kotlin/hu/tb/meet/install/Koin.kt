package hu.tb.meet.install

import hu.tb.meet.di.appModule
import hu.tb.meet.di.tokenModule
import hu.tb.meet.domain.JwtConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
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
            tokenModule(config = JwtConfig(issuer = configIssuer, audience = configAudience, secret = configSecret)),
            appModule
        )
    }
}
