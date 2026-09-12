package hu.tb.notification.data

import android.util.Log
import com.google.firebase.installations.FirebaseInstallations
import hu.tb.network.FidProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DeviceFidProvider : FidProvider {

    override suspend fun getFirebaseFid(): String? = suspendCancellableCoroutine { continuation ->
        FirebaseInstallations.getInstance().id
            .addOnSuccessListener { continuation.resume(it) }
            .addOnFailureListener {
                Log.w("MYTAG", "cannot read firebase installation id", it)
                continuation.resume(null)
            }
    }
}
