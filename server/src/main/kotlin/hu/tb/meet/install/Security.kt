package hu.tb.meet.install

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hu.tb.meet.domain.send.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    val configRealm = environment.config.property("jwt.realm").getString()
    val configAudience = environment.config.property("jwt.audience").getString()
    val configIssuer = environment.config.property("jwt.issuer").getString()
    val configSecret = environment.config.propertyOrNull("jwt.secret")?.getString() ?: "debug_build"

    authentication {
        jwt("auth-jwt") {
            realm = configRealm
            verifier(
                JWT.require(Algorithm.HMAC256(configSecret))
                    .withIssuer(configIssuer)
                    .withAudience(configAudience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(configAudience)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token is not valid or has expired"))
            }
        }
    }
}
