package hu.tb.meet.route

import hu.tb.meet.data.repository.DeviceFidRepository
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.DeviceFidReceive
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

suspend fun HttpClient.registerDeviceFid(token: String?, fid: String): HttpResponse =
    post("/registerDeviceFid") {
        contentType(ContentType.Application.Json)
        bearer(token)
        setBody(DeviceFidReceive(fid))
    }

suspend fun HttpClient.unregisterDeviceFid(token: String?, fid: String): HttpResponse =
    post("/unregisterDeviceFid") {
        contentType(ContentType.Application.Json)
        bearer(token)
        setBody(DeviceFidReceive(fid))
    }

class DeviceFidRouteTest {

    private val deviceFids = DeviceFidRepository()

    @Test
    fun `registering the same fid twice keeps a single device`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)
        val normal = client.tokenOf("anna", AccountType.NORMAL)
        val coachId = client.coaches(normal, "kovacs").single().coachId.toInt()

        assertEquals(HttpStatusCode.OK, client.registerDeviceFid(coach, "fid-1").status)
        assertEquals(HttpStatusCode.OK, client.registerDeviceFid(coach, "fid-1").status)

        assertEquals(listOf("fid-1"), deviceFids.getFids(AccountType.COACH, coachId))
    }

    @Test
    fun `a fid moves to the account that registered it last`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)
        val normal = client.tokenOf("anna", AccountType.NORMAL)
        val coachId = client.coaches(normal, "kovacs").single().coachId.toInt()

        client.registerDeviceFid(normal, "shared-device")
        client.registerDeviceFid(coach, "shared-device")

        assertEquals(listOf("shared-device"), deviceFids.getFids(AccountType.COACH, coachId))
        assertEquals(emptyList(), deviceFids.getFids(AccountType.NORMAL, 1))
    }

    @Test
    fun `one account keeps several devices side by side`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)
        val normal = client.tokenOf("anna", AccountType.NORMAL)
        val coachId = client.coaches(normal, "kovacs").single().coachId.toInt()

        client.registerDeviceFid(coach, "phone")
        client.registerDeviceFid(coach, "tablet")

        assertEquals(listOf("phone", "tablet"), deviceFids.getFids(AccountType.COACH, coachId).sorted())
    }

    @Test
    fun `both account types can register a device`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)
        val normal = client.tokenOf("anna", AccountType.NORMAL)

        assertEquals(HttpStatusCode.OK, client.registerDeviceFid(coach, "coach-device").status)
        assertEquals(HttpStatusCode.OK, client.registerDeviceFid(normal, "normal-device").status)
    }

    @Test
    fun `a blank fid is a bad request`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)

        val response = client.registerDeviceFid(coach, "   ")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Malformed request body", response.body<ErrorResponse>().message)
    }

    @Test
    fun `an over long fid is a bad request`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)

        val response = client.registerDeviceFid(coach, "f".repeat(513))

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Malformed request body", response.body<ErrorResponse>().message)
    }

    @Test
    fun `no token is unauthorized on every endpoint`() = withTestApp { client ->
        assertEquals(HttpStatusCode.Unauthorized, client.registerDeviceFid(null, "fid-1").status)
        assertEquals(HttpStatusCode.Unauthorized, client.unregisterDeviceFid(null, "fid-1").status)
    }

    @Test
    fun `unregistering removes the device and stays ok when it is already gone`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)
        val normal = client.tokenOf("anna", AccountType.NORMAL)
        val coachId = client.coaches(normal, "kovacs").single().coachId.toInt()
        client.registerDeviceFid(coach, "fid-1")

        assertEquals(HttpStatusCode.OK, client.unregisterDeviceFid(coach, "fid-1").status)

        assertEquals(emptyList(), deviceFids.getFids(AccountType.COACH, coachId))
        assertEquals(HttpStatusCode.OK, client.unregisterDeviceFid(coach, "fid-1").status)
    }

    @Test
    fun `one account cannot unregister another account device`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)
        val normal = client.tokenOf("anna", AccountType.NORMAL)
        val coachId = client.coaches(normal, "kovacs").single().coachId.toInt()
        client.registerDeviceFid(coach, "coach-device")

        assertEquals(HttpStatusCode.OK, client.unregisterDeviceFid(normal, "coach-device").status)

        assertEquals(listOf("coach-device"), deviceFids.getFids(AccountType.COACH, coachId))
    }

    @Test
    fun `deleting a profile leaves its fids to the client to unregister`() = withTestApp { client ->
        val coach = client.tokenOf("Kovacs Anna", AccountType.COACH)
        val normal = client.tokenOf("anna", AccountType.NORMAL)
        val coachId = client.coaches(normal, "kovacs").single().coachId.toInt()
        client.registerDeviceFid(coach, "fid-1")

        assertEquals(HttpStatusCode.NoContent, client.deleteProfile(coach).status)

        // deliberate: the delete endpoint does not unregister devices, the client does that first
        assertEquals(listOf("fid-1"), deviceFids.getFids(AccountType.COACH, coachId))
    }
}
