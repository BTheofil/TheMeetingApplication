package hu.tb.meet.route

import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.send.AuthResponse
import hu.tb.meet.domain.send.ErrorResponse
import hu.tb.meet.withTestApp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

private suspend fun HttpClient.tokenFor(
    username: String,
    password: String,
    type: AccountType
): String {
    register(username, password, type)
    return login(username, password, type).body<AuthResponse>().token
}

private suspend fun HttpClient.deleteProfile(token: String?): HttpResponse = delete("/profile") {
    if (token != null) header(HttpHeaders.Authorization, "Bearer $token")
}

class DeleteProfileRouteTest {

    @Test
    fun `own profile is deleted and can no longer log in`() = withTestApp { client ->
        val token = client.tokenFor("anna", "secret123", AccountType.NORMAL)

        assertEquals(HttpStatusCode.NoContent, client.deleteProfile(token).status)
        assertEquals(HttpStatusCode.Unauthorized, client.login("anna", "secret123", AccountType.NORMAL).status)
    }

    @Test
    fun `deleting twice leaves the second call unauthorized`() = withTestApp { client ->
        val token = client.tokenFor("anna", "secret123", AccountType.NORMAL)

        assertEquals(HttpStatusCode.NoContent, client.deleteProfile(token).status)
        val second = client.deleteProfile(token)
        assertEquals(HttpStatusCode.Unauthorized, second.status)
        assertEquals("Profile no longer exists", second.body<ErrorResponse>().message)
    }

    @Test
    fun `no token is unauthorized`() = withTestApp { client ->
        client.register("anna", "secret123", AccountType.NORMAL)

        assertEquals(HttpStatusCode.Unauthorized, client.deleteProfile(null).status)
        assertEquals(HttpStatusCode.OK, client.login("anna", "secret123", AccountType.NORMAL).status)
    }

    @Test
    fun `malformed token is unauthorized`() = withTestApp { client ->
        client.register("anna", "secret123", AccountType.NORMAL)

        assertEquals(HttpStatusCode.Unauthorized, client.deleteProfile("not-a-jwt").status)
        assertEquals(HttpStatusCode.OK, client.login("anna", "secret123", AccountType.NORMAL).status)
    }

    @Test
    fun `a normal token cannot delete the coach of the same name`() = withTestApp { client ->
        val normalToken = client.tokenFor("anna", "secret123", AccountType.NORMAL)
        client.register("anna", "secret123", AccountType.COACH)

        assertEquals(HttpStatusCode.NoContent, client.deleteProfile(normalToken).status)
        assertEquals(HttpStatusCode.OK, client.login("anna", "secret123", AccountType.COACH).status)
    }
}
