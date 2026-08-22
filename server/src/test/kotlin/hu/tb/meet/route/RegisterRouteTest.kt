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

suspend fun HttpClient.register(
    username: String,
    password: String,
    type: AccountType
): HttpResponse = post("/register") {
    contentType(ContentType.Application.Json)
    setBody(AuthReceive(username, password, type))
}

class RegisterRouteTest {

    @Test
    fun `new username is created and gets a token`() = withTestApp { client ->
        val response = client.register("anna", "secret123", AccountType.NORMAL)

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.body<AuthResponse>().token.isNotBlank())
    }

    @Test
    fun `same username twice for the same type is rejected`() = withTestApp { client ->
        assertEquals(HttpStatusCode.Created, client.register("anna", "secret123", AccountType.NORMAL).status)

        val second = client.register("anna", "other-password", AccountType.NORMAL)

        assertEquals(HttpStatusCode.Conflict, second.status)
        assertEquals("Username already taken", second.body<ErrorResponse>().message)
    }

    @Test
    fun `same username is allowed across different account types`() = withTestApp { client ->
        assertEquals(HttpStatusCode.Created, client.register("anna", "secret123", AccountType.NORMAL).status)
        assertEquals(HttpStatusCode.Created, client.register("anna", "secret123", AccountType.COACH).status)
    }

    @Test
    fun `unknown account type is a bad request`() = withTestApp { client ->
        val response = client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"anna","password":"secret123","type":"banana"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `missing field is a bad request`() = withTestApp { client ->
        val response = client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"anna"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
