package hu.tb.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import hu.tb.domain.AuthMode
import hu.tb.presentation.form.AuthFormScreen
import hu.tb.presentation.welcome.WelcomeScreen

@Stable
sealed interface Destination : NavKey {
    sealed interface AuthGraph : Destination {
        data object Welcome : AuthGraph
        data class Form(val mode: AuthMode) : AuthGraph
    }
}

@Composable
fun Navigator() {
    val backstack = remember { mutableStateListOf<Destination>(Destination.AuthGraph.Welcome) }

    NavDisplay(
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Destination.AuthGraph.Welcome> {
                WelcomeScreen(
                    onModeClick = { backstack.add(Destination.AuthGraph.Form(it)) }
                )
            }
            entry<Destination.AuthGraph.Form> { key ->
                AuthFormScreen(mode = key.mode)
            }
        }
    )
}
