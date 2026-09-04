package hu.tb.meeting

import android.app.Application
import hu.tb.dashboard.presentation.di.dashboardModule
import hu.tb.datastore.UserDatastoreRepository
import hu.tb.datastore.di.datastoreModule
import hu.tb.navigator.di.navigatorModule
import hu.tb.network.TokenProvider
import hu.tb.network.di.networkModule
import hu.tb.presentation.di.authModule
import hu.tb.profile.presentation.di.profileModule
import hu.tb.search.di.searchModule
import kotlinx.coroutines.flow.first
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MeetingApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MeetingApplication)
            modules(
                module {
                    single<TokenProvider> {
                        val datastore = get<UserDatastoreRepository>()
                        TokenProvider { datastore.userdataFlow().first().token.ifBlank { null } }
                    }
                },
                networkModule,
                datastoreModule,
                authModule,
                profileModule,
                navigatorModule,
                dashboardModule,
                searchModule
            )
        }
    }
}
