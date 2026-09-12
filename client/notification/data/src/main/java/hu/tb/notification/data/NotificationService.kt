package hu.tb.notification.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hu.tb.network.TokenProvider
import hu.tb.network.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class NotificationService : FirebaseMessagingService() {

    private val authRepository: AuthRepository by inject()
    private val tokenProvider: TokenProvider by inject()

    // replaces onNewToken: with installation-id registration enabled the SDK reports the
    // registered fid here instead of handing out a legacy token
    override fun onRegistered(fid: String) {
        super.onRegistered(fid)

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            if (tokenProvider.getTokenOrNull() == null) return@launch
            authRepository.registerDeviceFid()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        when (message.data["type"]) {
            TYPE_COACH_REQUEST -> showCoachRequest(
                senderId = message.data["normalId"].orEmpty(),
                senderName = message.data["normalName"].orEmpty()
            )
        }
    }

    private fun showCoachRequest(senderId: String, senderName: String) {
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_REQUESTS,
                getString(R.string.notification_channel_requests),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_REQUESTS)
            .setSmallIcon(applicationInfo.icon)
            .setContentTitle(getString(R.string.notification_coach_request_title))
            .setContentText(getString(R.string.notification_coach_request_text, senderName))
            .setContentIntent(requestsPendingIntent())
            .setAutoCancel(true)
            .build()

        manager.notify(senderId.hashCode(), notification)
    }

    private fun requestsPendingIntent(): PendingIntent? {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
            ?.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            ?.putExtra(
                NotificationIntent.EXTRA_DESTINATION,
                NotificationIntent.DESTINATION_REQUESTS
            )
            ?: return null

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private companion object {
        const val TYPE_COACH_REQUEST = "COACH_REQUEST"
        const val CHANNEL_REQUESTS = "coach_requests"
    }
}
