package hu.tb.meet.notification

import com.google.firebase.messaging.MulticastMessage

interface PushSender {
    fun send(fids: List<String>, message: MulticastMessage): List<String>
}
