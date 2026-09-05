package hu.tb.meet.route

import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.SearchReceive
import hu.tb.meet.domain.send.AuthResponse
import hu.tb.meet.domain.send.SearchResultResponse
import hu.tb.meet.domain.send.SubscriptionStatus
import hu.tb.meet.withTestApp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals

const val PASSWORD = "secret123"

fun HttpRequestBuilder.bearer(token: String?) {
    if (token != null) bearerAuth(token)
}

suspend fun HttpClient.tokenOf(username: String, type: AccountType): String {
    register(username, PASSWORD, type)
    return login(username, PASSWORD, type).body<AuthResponse>().token
}

suspend fun HttpClient.searchCoach(token: String?, name: String): HttpResponse = post("/searchCoach") {
    contentType(ContentType.Application.Json)
    bearer(token)
    setBody(SearchReceive(name))
}

suspend fun HttpClient.coaches(token: String, name: String) =
    searchCoach(token, name).body<SearchResultResponse>().coaches

class SearchRouteTest {

    @Test
    fun `finds a coach by a case-insensitive name fragment`() = withTestApp { client ->
        client.tokenOf("Kovacs Anna", AccountType.COACH)
        val normal = client.tokenOf("anna", AccountType.NORMAL)

        val found = client.coaches(normal, "oVaCs").single()

        assertEquals("Kovacs Anna", found.coachName)
        assertEquals(SubscriptionStatus.INIT, found.status)
    }

    @Test
    fun `no match returns an empty list`() = withTestApp { client ->
        client.tokenOf("Kovacs Anna", AccountType.COACH)
        val normal = client.tokenOf("anna", AccountType.NORMAL)

        assertEquals(emptyList(), client.coaches(normal, "zzz"))
    }

    @Test
    fun `only a normal profile can search`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)

        assertEquals(HttpStatusCode.Unauthorized, client.searchCoach(coach, "kovacs").status)
        assertEquals(HttpStatusCode.Unauthorized, client.searchCoach(null, "kovacs").status)
    }
}
