package hu.tb.navigator

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
    data object AuthRoot : Destination
    data object DashboardRoot : Destination

    @Stable
    sealed interface AuthGraph : NavKey {
        data object Welcome : AuthGraph
        data class Form(val mode: AuthMode) : AuthGraph
    }

    @Stable
    sealed interface DashboardGraph : NavKey {
        data object Calendar : DashboardGraph
    }
}

@Composable
fun Navigator() {
    val graphStack = remember {
        mutableStateListOf<Destination>(Destination.AuthRoot)
    }
    val authStack = remember {
        mutableStateListOf<Destination.AuthGraph>(Destination.AuthGraph.Welcome)
    }
    val dashboardStack = remember {
        mutableStateListOf<Destination.DashboardGraph>(Destination.DashboardGraph.Calendar)
    }

    NavDisplay(
        backStack = graphStack,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        popTransitionSpec = { fadeIn() togetherWith fadeOut() },
        entryProvider = entryProvider {
            entry<Destination.AuthRoot> {
                NavDisplay(
                    backStack = authStack,
                    transitionSpec = {
                        fadeIn() togetherWith ExitTransition.KeepUntilTransitionsFinished
                    },
                    entryProvider = entryProvider {
                        entry<Destination.AuthGraph.Welcome> {
                            WelcomeScreen(
                                onModeClick = { authStack.add(Destination.AuthGraph.Form(it)) }
                            )
                        }
                        entry<Destination.AuthGraph.Form> { key ->
                            AuthFormScreen(mode = key.mode)
                        }
                    }
                )
            }
            entry<Destination.DashboardRoot> {
                NavDisplay(
                    backStack = dashboardStack,
                    transitionSpec = {
                        slideIntoContainer(SlideDirection.Left) togetherWith
                                slideOutOfContainer(SlideDirection.Left)
                    },
                    popTransitionSpec = {
                        slideIntoContainer(SlideDirection.Right) togetherWith
                                slideOutOfContainer(SlideDirection.Right)
                    },
                    entryProvider = entryProvider {
                        entry<Destination.DashboardGraph.Calendar> { }
                    }
                )
            }
        }
    )
}
