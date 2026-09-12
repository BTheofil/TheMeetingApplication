package hu.tb.notification.data

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Registers this installation with FCM, which is what turns its fid into a valid push target.
 * Needs the firebase_messaging_installation_id_enabled manifest flag, without which the task
 * fails instead of registering.
 *
 * Deliberately kept out of [DeviceFidProvider]: the SDK reports a finished registration through
 * [NotificationService.onRegistered], which uploads the fid, which reads the provider again.
 * Registering from that path would retrigger the callback and loop.
 */
object FcmRegistration {
    fun ensure() {
        FirebaseMessaging.getInstance().register()
            .addOnFailureListener {
                Log.w("FcmRegistration", "fcm registration failed, pushes will not arrive", it)
            }
    }
}
