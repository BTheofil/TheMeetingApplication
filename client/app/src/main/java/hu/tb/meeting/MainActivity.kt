package hu.tb.meeting

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.navigator.Navigator
import hu.tb.navigator.NavigatorViewModel
import hu.tb.navigator.SessionState
import hu.tb.notification.data.NotificationIntent
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val navigatorViewModel: NavigatorViewModel by viewModel()

    private var openNotifications by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            navigatorViewModel.session.value is SessionState.Init
        }
        openNotifications = intent.opensNotifications()
        enableEdgeToEdge()
        setContent {
            MeetingTheme {
                Navigator(
                    viewModel = navigatorViewModel,
                    openNotifications = openNotifications,
                    onOpenNotificationsHandled = { openNotifications = false }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.opensNotifications()) openNotifications = true
    }

    private fun Intent.opensNotifications(): Boolean =
        getStringExtra(NotificationIntent.EXTRA_DESTINATION) ==
                NotificationIntent.DESTINATION_REQUESTS
}
