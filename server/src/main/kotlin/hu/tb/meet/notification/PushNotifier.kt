package hu.tb.meet.notification

import com.google.firebase.messaging.MulticastMessage
import hu.tb.meet.data.repository.DeviceFidRepository
import hu.tb.meet.domain.receive.AccountType
import org.slf4j.LoggerFactory

class PushNotifier(
    private val deviceFidRepository: DeviceFidRepository,
    private val pushSender: PushSender?,
) {
    private val logger = LoggerFactory.getLogger(PushNotifier::class.java)

    fun coachRequested(coachId: Int, normalId: Int, normalUsername: String) {
        val sender = pushSender ?: return

        val fids = deviceFidRepository.getFids(AccountType.COACH, coachId)
        if (fids.isEmpty()) {
            logger.debug("coach {} has no registered device", coachId)
            return
        }

        val message = MulticastMessage.builder()
            .addAllFids(fids)
            .putData("type", "COACH_REQUEST")
            .putData("normalId", normalId.toString())
            .putData("normalName", normalUsername)
            .build()

        val deadFids = runCatching { sender.send(fids, message) }
            .onFailure { logger.warn("push to coach $coachId failed", it) }
            .getOrDefault(emptyList())

        if (deadFids.isNotEmpty()) {
            logger.info("forgetting {} dead device fid(s)", deadFids.size)
            deviceFidRepository.forget(deadFids)
        }
    }
}
