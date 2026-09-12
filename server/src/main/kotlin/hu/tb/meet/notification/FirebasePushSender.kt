package hu.tb.meet.notification

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MessagingErrorCode
import com.google.firebase.messaging.MulticastMessage
import org.slf4j.LoggerFactory

class FirebasePushSender(private val app: FirebaseApp) : PushSender {

    private val logger = LoggerFactory.getLogger(FirebasePushSender::class.java)

    override fun send(fids: List<String>, message: MulticastMessage): List<String> {
        val response = FirebaseMessaging.getInstance(app).sendEachForMulticast(message)

        response.responses.forEachIndexed { index, result ->
            val exception = result.exception ?: return@forEachIndexed

            logger.warn(
                "push to fid {} rejected: {} - {}",
                fids[index],
                exception.messagingErrorCode,
                exception.message
            )
        }

        return response.responses.withIndex()
            .filter { (_, it) -> it.exception?.messagingErrorCode == MessagingErrorCode.UNREGISTERED }
            .map { (index, _) -> fids[index] }
    }
}
