package hu.tb.meet.di

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import hu.tb.meet.notification.FirebasePushSender
import hu.tb.meet.notification.PushNotifier
import hu.tb.meet.notification.PushSender
import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.notificationModule(): Module {
    val credentials = environment.config.propertyOrNull("firebase.credentials")?.getString()
        ?.takeIf { it.isNotBlank() }

    if (credentials == null) {
        val message = "firebase.credentials is not set, push notifications are disabled"
        if (developmentMode) {
            log.warn(message)
        } else {
            log.error("$message, pass FIREBASE_CREDENTIALS_JSON to the container")
        }
    }

    return module {
       credentials?.let { json ->
            single<PushSender> {
                val app = FirebaseApp.getApps().firstOrNull()
                    ?: FirebaseApp.initializeApp(
                        FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(json.byteInputStream()))
                            .build()
                    )
                FirebasePushSender(app)
            }
        }

        single { PushNotifier(get(), getOrNull()) }
    }
}
