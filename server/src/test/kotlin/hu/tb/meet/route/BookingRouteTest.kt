package hu.tb.meet.route

import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.BookReceive
import hu.tb.meet.domain.receive.CoachMonthReceive
import hu.tb.meet.domain.receive.Draft
import hu.tb.meet.domain.receive.MonthReceive
import hu.tb.meet.domain.receive.ScheduleDay
import hu.tb.meet.domain.receive.SlotReceive
import hu.tb.meet.domain.send.ErrorResponse
import hu.tb.meet.domain.send.SessionResult
import hu.tb.meet.domain.send.SubscriberResult
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private val MONDAY = LocalDate(2026, 9, 21)
private val MORNING = Draft(LocalTime.parse("09:00"), LocalTime.parse("10:00"))
private val NOON = Draft(LocalTime.parse("12:00"), LocalTime.parse("13:00"))

suspend fun HttpClient.bookSession(token: String?, sessionId: Int): HttpResponse = post("/bookSession") {
    contentType(ContentType.Application.Json)
    bearer(token)
    setBody(BookReceive(sessionId))
}

suspend fun HttpClient.freeSessions(token: String?, coachId: Int, date: LocalDate): HttpResponse =
    post("/freeSessions") {
        contentType(ContentType.Application.Json)
        bearer(token)
        setBody(CoachMonthReceive(coachId, date))
    }

suspend fun HttpClient.myBookings(token: String?): HttpResponse = get("/myBookings") {
    bearer(token)
}

suspend fun HttpClient.coachSessions(token: String?, date: LocalDate): HttpResponse = post("/coachSessions") {
    contentType(ContentType.Application.Json)
    bearer(token)
    setBody(MonthReceive(date))
}

private suspend fun HttpClient.free(token: String, coachId: Int) =
    freeSessions(token, coachId, MONDAY).body<List<SessionResult>>()

private suspend fun HttpClient.booked(token: String) = myBookings(token).body<List<SessionResult>>()

private suspend fun HttpClient.published(token: String) = coachSessions(token, MONDAY).body<List<SessionResult>>()

/** registers a normal account and gets the coach to accept it */
private suspend fun HttpClient.confirm(coachToken: String, normalName: String, coachId: Int): String {
    val normal = tokenOf(normalName, AccountType.NORMAL)
    requestToCoach(normal, coachId)
    val normalId = pendingRequests(coachToken).body<List<SubscriberResult>>()
        .single { it.normalName == normalName }.normalId.toInt()
    acceptRequest(coachToken, normalId)
    return normal
}

private data class Setup(val coach: String, val normal: String, val coachId: Int, val sessionId: Int)

/** a confirmed coach and normal pair with one published morning slot */
private suspend fun HttpClient.setup(): Setup {
    val coach = tokenOf("Kovacs Anna", AccountType.COACH)
    val scout = tokenOf("scout", AccountType.NORMAL)
    val coachId = coaches(scout, "kovacs").single().coachId.toInt()
    val normal = confirm(coach, "anna", coachId)

    uploadDrafts(coach, listOf(ScheduleDay(MONDAY, listOf(MORNING))))
    val sessionId = free(normal, coachId).single().id

    return Setup(coach, normal, coachId, sessionId)
}

class BookingRouteTest {

    @Test
    fun `a confirmed normal books a published slot`() = withTestApp { client ->
        val (_, normal, _, sessionId) = client.setup()

        assertEquals(HttpStatusCode.OK, client.bookSession(normal, sessionId).status)
    }

    @Test
    fun `a booked slot disappears from the free list`() = withTestApp { client ->
        val (_, normal, coachId, sessionId) = client.setup()

        client.bookSession(normal, sessionId)

        assertEquals(emptyList(), client.free(normal, coachId))
    }

    @Test
    fun `only the unbooked slots stay free`() = withTestApp { client ->
        val (coach, normal, coachId, sessionId) = client.setup()
        client.uploadDrafts(coach, listOf(ScheduleDay(MONDAY, listOf(NOON))))

        client.bookSession(normal, sessionId)

        assertEquals(listOf(NOON.start), client.free(normal, coachId).map { it.start })
    }

    @Test
    fun `booking the same slot twice answers conflict`() = withTestApp { client ->
        val (_, normal, _, sessionId) = client.setup()
        client.bookSession(normal, sessionId)

        val response = client.bookSession(normal, sessionId)

        assertEquals(HttpStatusCode.Conflict, response.status)
        assertEquals("This session is already taken", response.body<ErrorResponse>().message)
    }

    @Test
    fun `a second normal can not take a booked slot`() = withTestApp { client ->
        val (coach, normal, coachId, sessionId) = client.setup()
        val other = client.confirm(coach, "bela", coachId)
        client.bookSession(normal, sessionId)

        assertEquals(HttpStatusCode.Conflict, client.bookSession(other, sessionId).status)
        // the loser is not told who took it, the slot is simply gone from the free list
        assertEquals(emptyList(), client.free(other, coachId))
    }

    @Test
    fun `a pending subscription can not book or list`() = withTestApp { client ->
        val (_, _, coachId, sessionId) = client.setup()
        val pending = client.tokenOf("pending", AccountType.NORMAL)
        client.requestToCoach(pending, coachId)

        val response = client.bookSession(pending, sessionId)

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("This session can not be booked", response.body<ErrorResponse>().message)
        assertEquals(HttpStatusCode.NotFound, client.freeSessions(pending, coachId, MONDAY).status)
    }

    @Test
    fun `a stranger can not book or list`() = withTestApp { client ->
        val (_, _, coachId, sessionId) = client.setup()
        val stranger = client.tokenOf("stranger", AccountType.NORMAL)

        assertEquals(HttpStatusCode.NotFound, client.bookSession(stranger, sessionId).status)
        assertEquals(HttpStatusCode.NotFound, client.freeSessions(stranger, coachId, MONDAY).status)
    }

    @Test
    fun `a client of one coach can not book a slot of another`() = withTestApp { client ->
        val (_, normal, _, _) = client.setup()
        val otherCoach = client.tokenOf("Nagy Bela", AccountType.COACH)
        val otherCoachId = client.coaches(normal, "nagy").single().coachId.toInt()
        val otherClient = client.confirm(otherCoach, "bela", otherCoachId)
        client.uploadDrafts(otherCoach, listOf(ScheduleDay(MONDAY, listOf(NOON))))
        val foreignSession = client.free(otherClient, otherCoachId).single().id

        assertEquals(HttpStatusCode.NotFound, client.bookSession(normal, foreignSession).status)
    }

    @Test
    fun `an unknown session id can not be booked`() = withTestApp { client ->
        val (_, normal, _, _) = client.setup()

        assertEquals(HttpStatusCode.NotFound, client.bookSession(normal, 9999).status)
    }

    @Test
    fun `my bookings lists the session with the coach name`() = withTestApp { client ->
        val (_, normal, _, sessionId) = client.setup()
        assertEquals(emptyList(), client.booked(normal))

        client.bookSession(normal, sessionId)

        val mine = client.booked(normal).single()
        assertEquals(sessionId, mine.id)
        assertEquals(MONDAY, mine.date)
        assertEquals("Kovacs Anna", mine.counterpart)
    }

    @Test
    fun `the coach sees who booked each slot`() = withTestApp { client ->
        val (coach, normal, _, sessionId) = client.setup()
        client.uploadDrafts(coach, listOf(ScheduleDay(MONDAY, listOf(NOON))))
        assertTrue(client.published(coach).all { it.counterpart == null })

        client.bookSession(normal, sessionId)

        val sessions = client.published(coach).associateBy { it.start }
        assertEquals("anna", sessions.getValue(MORNING.start).counterpart)
        assertNull(sessions.getValue(NOON.start).counterpart)
    }

    @Test
    fun `deleting a booked session removes the booking`() = withTestApp { client ->
        val (coach, normal, _, sessionId) = client.setup()
        client.bookSession(normal, sessionId)

        val deleted = client.deleteSession(coach, SlotReceive(MONDAY, MORNING.start, MORNING.end))

        assertEquals(HttpStatusCode.OK, deleted.status)
        assertEquals(emptyList(), client.booked(normal))
    }

    @Test
    fun `deleting the booker profile frees the slot`() = withTestApp { client ->
        val (coach, normal, _, sessionId) = client.setup()
        client.bookSession(normal, sessionId)

        assertEquals(HttpStatusCode.NoContent, client.deleteProfile(normal).status)

        assertNull(client.published(coach).single().counterpart)
    }

    @Test
    fun `a coach account is forbidden on the booking endpoints`() = withTestApp { client ->
        val (coach, _, coachId, sessionId) = client.setup()

        assertEquals(HttpStatusCode.Forbidden, client.bookSession(coach, sessionId).status)
        assertEquals(HttpStatusCode.Forbidden, client.freeSessions(coach, coachId, MONDAY).status)
        assertEquals(HttpStatusCode.Forbidden, client.myBookings(coach).status)
    }

    @Test
    fun `no token is unauthorized on the booking endpoints`() = withTestApp { client ->
        assertEquals(HttpStatusCode.Unauthorized, client.bookSession(null, 1).status)
        assertEquals(HttpStatusCode.Unauthorized, client.freeSessions(null, 1, MONDAY).status)
        assertEquals(HttpStatusCode.Unauthorized, client.myBookings(null).status)
    }

    @Test
    fun `a malformed body answers with a json error`() = withTestApp { client ->
        val (_, normal, _, _) = client.setup()

        val response = client.post("/bookSession") {
            contentType(ContentType.Application.Json)
            bearer(normal)
            setBody("""{"sessionId":"nope"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Malformed request body", response.body<ErrorResponse>().message)
    }
}
