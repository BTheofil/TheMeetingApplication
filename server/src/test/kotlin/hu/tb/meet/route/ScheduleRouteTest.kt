package hu.tb.meet.route

import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.Draft
import hu.tb.meet.domain.receive.ScheduleDay
import hu.tb.meet.domain.receive.SlotReceive
import hu.tb.meet.domain.send.ErrorResponse
import hu.tb.meet.withTestApp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
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

private val MONDAY = LocalDate(2026, 9, 21)
private val TUESDAY = LocalDate(2026, 9, 22)

private fun draft(start: String, end: String) = Draft(LocalTime.parse(start), LocalTime.parse(end))

suspend fun HttpClient.uploadDrafts(token: String?, days: List<ScheduleDay>): HttpResponse = post("/uploadDrafts") {
    contentType(ContentType.Application.Json)
    bearer(token)
    setBody(days)
}

suspend fun HttpClient.deleteSession(token: String?, slot: SlotReceive): HttpResponse = delete("/deleteSession") {
    contentType(ContentType.Application.Json)
    bearer(token)
    setBody(slot)
}

private suspend fun HttpClient.upload(token: String, date: LocalDate, vararg drafts: Draft) =
    uploadDrafts(token, listOf(ScheduleDay(date, drafts.toList())))

private suspend fun HttpClient.delete(token: String, date: LocalDate, draft: Draft) =
    deleteSession(token, SlotReceive(date, draft.start, draft.end))

private suspend fun HttpClient.coach(name: String = "Kovacs Anna") = tokenOf(name, AccountType.COACH)

class ScheduleRouteTest {

    @Test
    fun `every uploaded slot of a day is stored`() = withTestApp { client ->
        val token = client.coach()
        val morning = draft("09:00", "10:00")
        val noon = draft("12:00", "13:00")

        assertEquals(HttpStatusCode.OK, client.upload(token, MONDAY, morning, noon).status)

        assertEquals(HttpStatusCode.OK, client.delete(token, MONDAY, morning).status)
        assertEquals(HttpStatusCode.OK, client.delete(token, MONDAY, noon).status)
    }

    @Test
    fun `a later upload extends the day instead of replacing it`() = withTestApp { client ->
        val token = client.coach()
        val morning = draft("09:00", "10:00")
        val afternoon = draft("15:00", "16:00")

        client.upload(token, MONDAY, morning)
        client.upload(token, MONDAY, afternoon)

        assertEquals(HttpStatusCode.OK, client.delete(token, MONDAY, morning).status)
        assertEquals(HttpStatusCode.OK, client.delete(token, MONDAY, afternoon).status)
    }

    @Test
    fun `uploading the same slot twice stores it once`() = withTestApp { client ->
        val token = client.coach()
        val morning = draft("09:00", "10:00")

        client.upload(token, MONDAY, morning)
        assertEquals(HttpStatusCode.OK, client.upload(token, MONDAY, morning).status)

        assertEquals(HttpStatusCode.OK, client.delete(token, MONDAY, morning).status)
        assertEquals(HttpStatusCode.NotFound, client.delete(token, MONDAY, morning).status)
    }

    @Test
    fun `days of one upload are kept apart`() = withTestApp { client ->
        val token = client.coach()
        val morning = draft("09:00", "10:00")

        client.uploadDrafts(
            token, listOf(
                ScheduleDay(MONDAY, listOf(morning)),
                ScheduleDay(TUESDAY, listOf(morning))
            )
        )

        assertEquals(HttpStatusCode.OK, client.delete(token, MONDAY, morning).status)
        assertEquals(HttpStatusCode.OK, client.delete(token, TUESDAY, morning).status)
    }

    @Test
    fun `an empty upload is accepted and stores nothing`() = withTestApp { client ->
        val token = client.coach()

        assertEquals(HttpStatusCode.OK, client.uploadDrafts(token, emptyList()).status)
        assertEquals(HttpStatusCode.OK, client.upload(token, MONDAY).status)

        assertEquals(HttpStatusCode.NotFound, client.delete(token, MONDAY, draft("09:00", "10:00")).status)
    }

    @Test
    fun `deleting an unknown slot answers with a json error`() = withTestApp { client ->
        val token = client.coach()

        val response = client.delete(token, MONDAY, draft("09:00", "10:00"))

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Targeted slot can not be found", response.body<ErrorResponse>().message)
    }

    @Test
    fun `deleting keeps the slot when the end time does not match`() = withTestApp { client ->
        val token = client.coach()
        val morning = draft("09:00", "10:00")
        client.upload(token, MONDAY, morning)

        assertEquals(HttpStatusCode.NotFound, client.delete(token, MONDAY, draft("09:00", "11:00")).status)

        assertEquals(HttpStatusCode.OK, client.delete(token, MONDAY, morning).status)
    }

    @Test
    fun `deleting only touches the targeted day`() = withTestApp { client ->
        val token = client.coach()
        val morning = draft("09:00", "10:00")
        client.upload(token, MONDAY, morning)
        client.upload(token, TUESDAY, morning)

        client.delete(token, MONDAY, morning)

        assertEquals(HttpStatusCode.OK, client.delete(token, TUESDAY, morning).status)
    }

    @Test
    fun `a coach only writes and deletes its own slots`() = withTestApp { client ->
        val anna = client.coach("Kovacs Anna")
        val bela = client.coach("Nagy Bela")
        val morning = draft("09:00", "10:00")

        client.upload(anna, MONDAY, morning)
        // the same slot on the same day is a separate row for the other coach
        assertEquals(HttpStatusCode.OK, client.upload(bela, MONDAY, morning).status)

        // bela deleting only removes his own row, anna's stays
        assertEquals(HttpStatusCode.OK, client.delete(bela, MONDAY, morning).status)
        assertEquals(HttpStatusCode.NotFound, client.delete(bela, MONDAY, morning).status)
        assertEquals(HttpStatusCode.OK, client.delete(anna, MONDAY, morning).status)
    }

    @Test
    fun `a normal account is forbidden on every endpoint`() = withTestApp { client ->
        val token = client.tokenOf("anna", AccountType.NORMAL)
        val morning = draft("09:00", "10:00")

        assertEquals(
            HttpStatusCode.Forbidden,
            client.uploadDrafts(token, listOf(ScheduleDay(MONDAY, listOf(morning)))).status
        )
        assertEquals(
            HttpStatusCode.Forbidden,
            client.deleteSession(token, SlotReceive(MONDAY, morning.start, morning.end)).status
        )
    }

    @Test
    fun `no token is unauthorized on every endpoint`() = withTestApp { client ->
        val morning = draft("09:00", "10:00")

        assertEquals(
            HttpStatusCode.Unauthorized,
            client.uploadDrafts(null, listOf(ScheduleDay(MONDAY, listOf(morning)))).status
        )
        assertEquals(
            HttpStatusCode.Unauthorized,
            client.deleteSession(null, SlotReceive(MONDAY, morning.start, morning.end)).status
        )
    }

    @Test
    fun `a malformed body answers with a json error`() = withTestApp { client ->
        val token = client.coach()

        val response = client.post("/uploadDrafts") {
            contentType(ContentType.Application.Json)
            bearer(token)
            setBody("""[{"date":"not-a-date","drafts":[]}]""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Malformed request body", response.body<ErrorResponse>().message)
    }
}
