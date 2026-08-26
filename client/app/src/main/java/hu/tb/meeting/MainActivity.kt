package hu.tb.meeting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.navigator.Navigator
import hu.tb.navigator.NavigatorViewModel
import hu.tb.navigator.SessionState
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val navigatorViewModel: NavigatorViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            navigatorViewModel.session.value is SessionState.Init
        }
        enableEdgeToEdge()
        setContent {
            MeetingTheme {
                Navigator(viewModel = navigatorViewModel)
            }
        }
    }
}
