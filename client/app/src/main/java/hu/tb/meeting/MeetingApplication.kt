package hu.tb.meeting

import android.app.Application
import hu.tb.presentation.di.authModule
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MeetingApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            modules(
                authModule
            )
        }
    }
}