package hu.tb.meet.notification

import com.google.firebase.messaging.MulticastMessage
import hu.tb.meet.data.repository.DeviceFidRepository
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.route.coaches
import hu.tb.meet.route.registerDeviceFid
import hu.tb.meet.route.tokenOf
import hu.tb.meet.withTestApp
import io.ktor.client.HttpClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// the Firebase SDK exposes no public getters on MulticastMessage, so the payload
// itself cannot be asserted here, only which fids it was addressed to
private class RecordingPushSender(private val dead: List<String> = emptyList()) : PushSender {

    var sentFids: List<String>? = null
        private set
    var sentMessage: MulticastMessage? = null
        private set

    override fun send(fids: List<String>, message: MulticastMessage): List<String> {
        sentFids = fids
        sentMessage = message
        return dead
    }
}

private class ThrowingPushSender : PushSender {
    override fun send(fids: List<String>, message: MulticastMessage): List<String> =
        throw IllegalStateException("firebase is down")
}

private suspend fun HttpClient.coachWith(vararg fids: String): Int {
    val coach = tokenOf("Kovacs Anna", AccountType.COACH)
    val normal = tokenOf("anna", AccountType.NORMAL)
    fids.forEach { registerDeviceFid(coach, it) }
    return coaches(normal, "kovacs").single().coachId.toInt()
}

class PushNotifierTest {

    private val deviceFids = DeviceFidRepository()

    @Test
    fun `every device of the coach is addressed`() = withTestApp { client ->
        val coachId = client.coachWith("phone", "tablet")
        val sender = RecordingPushSender()

        PushNotifier(deviceFids, sender).coachRequested(coachId, normalId = 1, normalUsername = "anna")

        assertEquals(listOf("phone", "tablet"), sender.sentFids?.sorted())
        assertNotNull(sender.sentMessage)
    }

    @Test
    fun `fids firebase rejects are forgotten and the rest are kept`() = withTestApp { client ->
        val coachId = client.coachWith("phone", "tablet")
        val sender = RecordingPushSender(dead = listOf("phone"))

        PushNotifier(deviceFids, sender).coachRequested(coachId, 1, "anna")

        assertEquals(listOf("tablet"), deviceFids.getFids(AccountType.COACH, coachId))
    }

    @Test
    fun `a sender that throws does not reach the caller and keeps the devices`() = withTestApp { client ->
        val coachId = client.coachWith("phone")

        PushNotifier(deviceFids, ThrowingPushSender()).coachRequested(coachId, 1, "anna")

        assertEquals(listOf("phone"), deviceFids.getFids(AccountType.COACH, coachId))
    }

    @Test
    fun `nothing is sent when the coach has no device`() = withTestApp { client ->
        val coachId = client.coachWith()
        val sender = RecordingPushSender()

        PushNotifier(deviceFids, sender).coachRequested(coachId, 1, "anna")

        assertNull(sender.sentFids)
        assertNull(sender.sentMessage)
    }

    @Test
    fun `a notifier without a sender stays silent instead of failing`() = withTestApp { client ->
        val coachId = client.coachWith("phone")

        PushNotifier(deviceFids, pushSender = null).coachRequested(coachId, 1, "anna")

        assertTrue(deviceFids.getFids(AccountType.COACH, coachId).isNotEmpty())
    }
}
