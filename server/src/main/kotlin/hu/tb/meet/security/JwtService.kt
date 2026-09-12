package hu.tb.meet.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hu.tb.meet.domain.receive.AccountType
import java.time.Instant
import java.time.temporal.ChronoUnit

class JwtService(
    private val issuer: String,
    private val audience: String,
    private val secret: String,
) {
    fun generate(username: String, type: AccountType): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withClaim("type", type.name)
            .withExpiresAt(Instant.now().plus(5, ChronoUnit.DAYS))
            .sign(Algorithm.HMAC256(secret))
}
