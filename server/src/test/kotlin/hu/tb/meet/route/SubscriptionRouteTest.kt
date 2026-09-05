package hu.tb.meet.route

import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.RequestReceive
import hu.tb.meet.domain.receive.ResolveReceive
import hu.tb.meet.domain.send.CoachResult
import hu.tb.meet.domain.send.SubscriberResult
import hu.tb.meet.domain.send.SubscriptionStatus
import hu.tb.meet.withTestApp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals

suspend fun HttpClient.requestToCoach(token: String?, coachId: Int): HttpResponse = post("/requestToCoach") {
    contentType(ContentType.Application.Json)
    bearer(token)
    setBody(RequestReceive(coachId))
}

suspend fun HttpClient.pendingRequests(token: String?): HttpResponse = post("/showPendingRequests") {
    bearer(token)
}

suspend fun HttpClient.acceptRequest(token: String?, normalId: Int): HttpResponse = post("/acceptRequest") {
    contentType(ContentType.Application.Json)
    bearer(token)
    setBody(ResolveReceive(normalId))
}

suspend fun HttpClient.rejectRequest(token: String?, normalId: Int): HttpResponse = post("/rejectRequest") {
    contentType(ContentType.Application.Json)
    bearer(token)
    setBody(ResolveReceive(normalId))
}

suspend fun HttpClient.myCoaches(token: String?): HttpResponse = get("/myCoaches") {
    bearer(token)
}

private suspend fun HttpClient.inbox(token: String) = pendingRequests(token).body<List<SubscriberResult>>()

private suspend fun HttpClient.coachList(token: String) = myCoaches(token).body<List<CoachResult>>()

private suspend fun HttpClient.statusOf(token: String) = coaches(token, "kovacs").single().status

/** Registers a coach and a normal profile, returns (coachToken, normalToken, coachId). */
private suspend fun HttpClient.pair(): Triple<String, String, Int> {
    val coach = tokenOf("Kovacs Anna", AccountType.COACH)
    val normal = tokenOf("anna", AccountType.NORMAL)
    return Triple(coach, normal, coaches(normal, "kovacs").single().coachId.toInt())
}

class SubscriptionRouteTest {

    @Test
    fun `a request lands in the coach inbox and shows as pending`() = withTestApp { client ->
        val (coach, normal, coachId) = client.pair()

        assertEquals(HttpStatusCode.OK, client.requestToCoach(normal, coachId).status)

        assertEquals("anna", client.inbox(coach).single().normalName)
        assertEquals(SubscriptionStatus.PENDING, client.statusOf(normal))
    }

    @Test
    fun `a rejected request goes back to init and can be sent again`() = withTestApp { client ->
        val (coach, normal, coachId) = client.pair()
        client.requestToCoach(normal, coachId)
        val normalId = client.inbox(coach).single().normalId.toInt()

        assertEquals(HttpStatusCode.OK, client.rejectRequest(coach, normalId).status)

        assertEquals(SubscriptionStatus.INIT, client.statusOf(normal))
        assertEquals(emptyList(), client.inbox(coach))
        assertEquals(emptyList(), client.coachList(normal))
        assertEquals(HttpStatusCode.OK, client.requestToCoach(normal, coachId).status)
    }

    @Test
    fun `an accepted request puts the coach into my coaches`() = withTestApp { client ->
        val (coach, normal, coachId) = client.pair()
        client.requestToCoach(normal, coachId)
        val normalId = client.inbox(coach).single().normalId.toInt()

        assertEquals(HttpStatusCode.OK, client.acceptRequest(coach, normalId).status)

        assertEquals("Kovacs Anna", client.coachList(normal).single().coachName)
        assertEquals(SubscriptionStatus.ACCEPTED, client.statusOf(normal))
        assertEquals(emptyList(), client.inbox(coach))
    }

    @Test
    fun `the wrong account type is unauthorized on every endpoint`() = withTestApp { client ->
        val (coach, normal, coachId) = client.pair()

        assertEquals(HttpStatusCode.Unauthorized, client.requestToCoach(coach, coachId).status)
        assertEquals(HttpStatusCode.Unauthorized, client.pendingRequests(normal).status)
        assertEquals(HttpStatusCode.Unauthorized, client.acceptRequest(normal, 1).status)
        assertEquals(HttpStatusCode.Unauthorized, client.rejectRequest(normal, 1).status)
        assertEquals(HttpStatusCode.Unauthorized, client.myCoaches(coach).status)
    }

    @Test
    fun `no token is unauthorized on every endpoint`() = withTestApp { client ->
        assertEquals(HttpStatusCode.Unauthorized, client.requestToCoach(null, 1).status)
        assertEquals(HttpStatusCode.Unauthorized, client.pendingRequests(null).status)
        assertEquals(HttpStatusCode.Unauthorized, client.acceptRequest(null, 1).status)
        assertEquals(HttpStatusCode.Unauthorized, client.rejectRequest(null, 1).status)
        assertEquals(HttpStatusCode.Unauthorized, client.myCoaches(null).status)
    }
}
