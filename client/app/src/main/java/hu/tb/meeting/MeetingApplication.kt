package hu.tb.meeting

import android.app.Application
import hu.tb.dashboard.presentation.di.dashboardModule
import hu.tb.datastore.di.datastoreModule
import hu.tb.navigator.di.navigatorModule
import hu.tb.network.di.networkModule
import hu.tb.presentation.di.authModule
import hu.tb.profile.presentation.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MeetingApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MeetingApplication)
            modules(
                networkModule,
                datastoreModule,
                authModule,
                profileModule,
                navigatorModule,
                dashboardModule
            )
        }
    }
}
