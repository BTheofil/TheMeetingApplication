package hu.tb.meet.route

import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.AuthReceive
import hu.tb.meet.domain.send.AuthResponse
import hu.tb.meet.domain.send.ErrorResponse
import hu.tb.meet.withTestApp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

suspend fun HttpClient.login(
    username: String,
    password: String,
    type: AccountType
): HttpResponse = post("/login") {
    contentType(ContentType.Application.Json)
    setBody(AuthReceive(username, password, type))
}

class LoginRouteTest {

    @Test
    fun `correct credentials return a token`() = withTestApp { client ->
        client.register("anna", "secret123", AccountType.NORMAL)

        val response = client.login("anna", "secret123", AccountType.NORMAL)

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.body<AuthResponse>().token.isNotBlank())
    }

    @Test
    fun `wrong password is unauthorized`() = withTestApp { client ->
        client.register("anna", "secret123", AccountType.NORMAL)

        val response = client.login("anna", "wrong", AccountType.NORMAL)

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("Invalid username or password", response.body<ErrorResponse>().message)
    }

    @Test
    fun `unknown username is unauthorized`() = withTestApp { client ->
        val response = client.login("nobody", "secret123", AccountType.NORMAL)

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `unknown username and wrong password are indistinguishable`() = withTestApp { client ->
        client.register("anna", "secret123", AccountType.NORMAL)

        val wrongPassword = client.login("anna", "wrong", AccountType.NORMAL)
        val unknownUser = client.login("nobody", "secret123", AccountType.NORMAL)

        assertEquals(wrongPassword.status, unknownUser.status)
        assertEquals(wrongPassword.body<ErrorResponse>().message, unknownUser.body<ErrorResponse>().message)
    }

    @Test
    fun `right credentials but wrong account type is unauthorized`() = withTestApp { client ->
        client.register("anna", "secret123", AccountType.NORMAL)

        val response = client.login("anna", "secret123", AccountType.COACH)

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
