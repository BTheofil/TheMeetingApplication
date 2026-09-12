package hu.tb.meet.notification

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MessagingErrorCode
import com.google.firebase.messaging.MulticastMessage

class FirebasePushSender(private val app: FirebaseApp) : PushSender {

    override fun send(fids: List<String>, message: MulticastMessage): List<String> {
        val response = FirebaseMessaging.getInstance(app).sendEachForMulticast(message)
        return response.responses.withIndex()
            .filter { (_, it) -> it.exception?.messagingErrorCode == MessagingErrorCode.UNREGISTERED }
            .map { (index, _) -> fids[index] }
    }
}
