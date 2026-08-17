package hu.tb.meet.install

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hu.tb.meet.domain.send.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

const val JWT_AUTH = "auth-jwt"

fun Application.configureSecurity() {
    val jwtConfig = environment.config.config("jwt")
    val realm = jwtConfig.property("realm").getString()
    val issuer = jwtConfig.property("issuer").getString()
    val audience = jwtConfig.property("audience").getString()
    val secret = jwtConfig.property("secret").getString()

    authentication {
        jwt(JWT_AUTH) {
            this.realm = realm
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(audience)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token is not valid or has expired"))
            }
        }
    }
}
