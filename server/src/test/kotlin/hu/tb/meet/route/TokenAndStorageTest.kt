package hu.tb.meet.route

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hu.tb.meet.data.repository.AuthRepository
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.send.AuthResponse
import hu.tb.meet.testJwtConfig
import hu.tb.meet.withTestApp
import io.ktor.client.call.*
import java.time.Instant
import kotlin.test.*

class TokenAndStorageTest {

    @Test
    fun `issued token carries the username and type claims`() = withTestApp { client ->
        val token = client.register("anna", "secret123", AccountType.COACH).body<AuthResponse>().token

        val decoded = JWT.decode(token)

        assertEquals("anna", decoded.getClaim("username").asString())
        assertEquals(AccountType.COACH.name, decoded.getClaim("type").asString())
        assertTrue(decoded.expiresAtAsInstant.isAfter(Instant.now()))
    }

    @Test
    fun `token from login verifies against the configured secret issuer and audience`() = withTestApp { client ->
        client.register("anna", "secret123", AccountType.NORMAL)
        val token = client.login("anna", "secret123", AccountType.NORMAL).body<AuthResponse>().token

        val jwt = testJwtConfig()
        val verifier = JWT.require(Algorithm.HMAC256(jwt.secret))
            .withIssuer(jwt.issuer)
            .withAudience(jwt.audience)
            .build()

        // throws if signature, issuer, audience or expiry do not match
        assertEquals("anna", verifier.verify(token).getClaim("username").asString())
    }

    @Test
    fun `password is stored as a bcrypt hash and not in plain text`() = withTestApp { client ->
        client.register("anna", "secret123", AccountType.NORMAL)

        val stored = AuthRepository().find(AccountType.NORMAL, "anna")

        assertNotNull(stored)
        assertNotEquals("secret123", stored.passwordHash)
        assertTrue(BCrypt.verifyer().verify("secret123".toCharArray(), stored.passwordHash).verified)
    }
}
