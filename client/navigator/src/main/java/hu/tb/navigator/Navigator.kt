package hu.tb.navigator

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import hu.tb.dashboard.presentation.DashboardScreen
import hu.tb.design_system.component.SessionExpiredDialog
import hu.tb.domain.AuthMode
import hu.tb.presentation.form.AuthFormScreen
import hu.tb.presentation.welcome.WelcomeScreen
import hu.tb.profile.presentation.ProfileScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

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
        data object Profile : DashboardGraph
    }
}

@Composable
fun Navigator(viewModel: NavigatorViewModel) {
    val sessionState by viewModel.session.collectAsStateWithLifecycle()

    if (sessionState is SessionState.Init) return

    val graphStack = remember {
        mutableStateListOf(
            if (viewModel.session.value is SessionState.NoUserSavedData) Destination.AuthRoot
            else Destination.DashboardRoot
        )
    }
    val authStack =
        remember { mutableStateListOf<Destination.AuthGraph>(Destination.AuthGraph.Welcome) }
    val dashboardStack =
        remember { mutableStateListOf<Destination.DashboardGraph>(Destination.DashboardGraph.Calendar) }

    NavDisplay(
        backStack = graphStack,
        entryProvider = entryProvider {
            entry<Destination.AuthRoot> {
                NavDisplay(
                    backStack = authStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    transitionSpec = {
                        fadeIn(tween(easing = LinearEasing), initialAlpha = .4f) togetherWith
                                fadeOut(tween(easing = LinearEasing))
                    },
                    popTransitionSpec = {
                        EnterTransition.None togetherWith fadeOut(tween(easing = LinearEasing))
                    },
                    predictivePopTransitionSpec = { _ ->
                        EnterTransition.None togetherWith fadeOut(tween())
                    },
                    entryProvider = entryProvider {
                        entry<Destination.AuthGraph.Welcome> {
                            WelcomeScreen(
                                onModeClick = { authStack.add(Destination.AuthGraph.Form(it)) }
                            )
                        }
                        entry<Destination.AuthGraph.Form> { key ->
                            AuthFormScreen(
                                viewModel = koinViewModel { parametersOf(key.mode) },
                                navigationRequest = {
                                    graphStack.add(Destination.DashboardRoot)
                                    graphStack.remove(Destination.AuthRoot)
                                })
                        }
                    }
                )
            }
            entry<Destination.DashboardRoot> {
                LaunchedEffect(Unit) { viewModel.refreshToken() }

                NavDisplay(
                    backStack = dashboardStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        entry<Destination.DashboardGraph.Calendar> {
                            DashboardScreen(
                                onProfileClick = {
                                    dashboardStack.add(Destination.DashboardGraph.Profile)
                                }
                            )
                        }
                        entry<Destination.DashboardGraph.Profile> {
                            ProfileScreen(
                                onBack = {
                                    dashboardStack.remove(Destination.DashboardGraph.Profile)
                                },
                                onDeleted = {
                                    viewModel.clearUserData()
                                    graphStack.add(Destination.AuthRoot)
                                    graphStack.remove(Destination.DashboardRoot)
                                    dashboardStack.clear()
                                    dashboardStack.add(Destination.DashboardGraph.Calendar)
                                    authStack.clear()
                                    authStack.add(Destination.AuthGraph.Welcome)
                                }
                            )
                        }
                    }
                )

                if (sessionState is SessionState.Expired) {
                    SessionExpiredDialog(
                        onConfirm = {
                            viewModel.clearUserData()
                            graphStack.add(Destination.AuthRoot)
                            graphStack.remove(Destination.DashboardRoot)
                            authStack.clear()
                            authStack.add(Destination.AuthGraph.Welcome)
                            authStack.add(Destination.AuthGraph.Form(AuthMode.LOGIN))
                        }
                    )
                }
            }
        }
    )
}
