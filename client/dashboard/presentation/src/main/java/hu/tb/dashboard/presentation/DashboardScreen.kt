package hu.tb.dashboard.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.dashboard.domain.CoachItem
import hu.tb.dashboard.domain.FreeSession
import hu.tb.dashboard.domain.SessionItem
import hu.tb.dashboard.presentation.component.OpenSlotCard
import hu.tb.dashboard.presentation.component.SessionCard
import hu.tb.dashboard.presentation.component.calendar.CollapsibleCalendar
import hu.tb.dashboard.presentation.component.calendar.CollapsibleCalendarParameter
import hu.tb.dashboard.presentation.component.coach.CoachOpenHoursCard
import hu.tb.dashboard.presentation.component.coach.DiscoverCoachesCard
import hu.tb.dashboard.presentation.component.coach.MyCoachesSection
import hu.tb.dashboard.presentation.component.common.SectionHeader
import hu.tb.dashboard.presentation.util.formatSectionLabel
import hu.tb.datastore.ProfileType
import hu.tb.design_system.Icons
import hu.tb.design_system.component.CardComponent
import hu.tb.design_system.component.CountdownSnackbar
import hu.tb.design_system.component.CountdownSnackbarVisuals
import hu.tb.design_system.modifier.glowBackground
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Clock

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    navigationRequest: (NavigationRequest) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    RequestNotificationPermission(state.profileType)

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is DashboardEvent.Failed ->
                    snackbarHostState.showSnackbar(
                        visuals = CountdownSnackbarVisuals(message = it.errorMessage)
                    )
            }
        }
    }

    DashboardScreen(
        snackbarHostState = snackbarHostState,
        state = state,
        action = { dashboardAction ->
            when (dashboardAction) {
                is DashboardAction.OnDateSelect -> viewModel.onDateSelected(dashboardAction.date)
                is NavigationRequest -> navigationRequest(dashboardAction)
                is DashboardAction.BookSession -> viewModel.bookSession(dashboardAction.freeSession)
            }
        }
    )
}

@Composable
private fun RequestNotificationPermission(profileType: ProfileType?) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    if (profileType != ProfileType.COACH) return

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* denied */ }

    LaunchedEffect(Unit) {
        val isGranted =
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED

        if (!isGranted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
private fun DashboardScreen(
    snackbarHostState: SnackbarHostState,
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .glowBackground()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    CountdownSnackbar(snackbarData = data)
                }
            },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        IconButton(onClick = { action(DashboardAction.OnProfileClick) }) {
                            Icon(
                                painter = painterResource(Icons.person),
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Meeting",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        if (state.profileType == ProfileType.COACH)
                            IconButton(onClick = { action(DashboardAction.OnNotificationClick) }) {
                                Icon(
                                    painter = painterResource(Icons.notifications),
                                    contentDescription = "notification icon",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .screenPadding()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CollapsibleCalendar(
                    calendarParameter = CollapsibleCalendarParameter(
                        state.bookedSessions, state.freeSessions, state.today, state.selectedDate
                    ),
                    action = action
                )
                SelectedDayBooked(state = state)
                if (state.profileType == ProfileType.NORMAL) {
                    SelectedDayFreeHours(state = state, action = action)
                }
                RoleSection(state = state, action = action)
            }
        }
    }
}

@Composable
private fun RoleSection(
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    when (state.profileType) {
        null -> Unit

        ProfileType.COACH -> CoachOpenHoursCard(
            onCreateOpenHours = { action(DashboardAction.OnCreateOpenHoursClick) }
        )

        ProfileType.NORMAL -> Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            MyCoachesSection(
                isLoading = state.isMyCoachesLoading,
                coaches = state.myCoaches,
            )
            DiscoverCoachesCard(
                onDiscoverCoaches = { action(DashboardAction.OnDiscoverCoachesClick) }
            )
        }
    }
}

@Composable
private fun SelectedDayBooked(
    state: DashboardState,
) {
    val sessions = state.getBookedSessions(state.selectedDate)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = 4.dp),
            title = "Booked · ${state.selectedDate.formatSectionLabel(state.today)}"
        )
        if (sessions.isEmpty()) {
            CardComponent {
                Text(
                    modifier = Modifier.padding(20.dp),
                    text = "No sessions on this day.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            sessions.forEach { session ->
                SessionCard(
                    session = session
                )
            }
        }
    }
}

@Composable
private fun SelectedDayFreeHours(
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    val slots = state.getFreeSessions(state.selectedDate)
    if (slots.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = 4.dp),
            title = "Free hours"
        )
        slots.forEach { slot ->
            OpenSlotCard(
                slot = slot,
                coachName = state.coachNameOf(slot.coachId),
                onClick = { action(DashboardAction.BookSession(slot)) }
            )
        }
    }
}

private val previewCoaches = listOf(
    CoachItem(id = 1, name = "Anna Kovács"),
    CoachItem(id = 2, name = "Márk Szabó"),
    CoachItem(id = 3, name = "Júlia Papp")
)

private fun previewState(
    profileType: ProfileType,
    coaches: List<CoachItem> = previewCoaches
): DashboardState {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val sessions = listOf(
        SessionItem(
            id = "s1",
            counterpartName = "Anna Kovács",
            date = today,
            start = LocalTime(9, 0),
            end = LocalTime(10, 0),
            isNext = true
        ),
        SessionItem(
            id = "s2",
            counterpartName = "Júlia Papp",
            date = today,
            start = LocalTime(17, 30),
            end = LocalTime(18, 0),
        ),
        SessionItem(
            id = "s3",
            counterpartName = "Márk Szabó",
            date = today.plus(1, DateTimeUnit.DAY),
            start = LocalTime(18, 30),
            end = LocalTime(19, 0),
        )
    )
    val slots = listOf(
        FreeSession(
            id = 1,
            coachId = 1,
            date = today,
            start = LocalTime(15, 0),
            end = LocalTime(16, 0)
        ),
        FreeSession(
            id = 2,
            coachId = 2,
            date = today.plus(2, DateTimeUnit.DAY),
            start = LocalTime(10, 0),
            end = LocalTime(11, 0),
        )
    )

    return DashboardState(
        profileType = profileType,
        today = today,
        selectedDate = today,
        bookedSessions = sessions,
        freeSessions = slots,
        myCoaches = coaches
    )
}

@PreviewLightDark
@Composable
private fun DashboardScreenCoachPreview() {
    MeetingTheme {
        DashboardScreen(
            snackbarHostState = SnackbarHostState(),
            state = previewState(ProfileType.COACH, coaches = emptyList()),
            action = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun DashboardScreenClientPreview() {
    MeetingTheme {
        DashboardScreen(
            snackbarHostState = SnackbarHostState(),
            state = previewState(ProfileType.NORMAL),
            action = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun DashboardScreenNoCoachesPreview() {
    MeetingTheme {
        DashboardScreen(
            snackbarHostState = SnackbarHostState(),
            state = previewState(ProfileType.NORMAL, coaches = emptyList()),
            action = {}
        )
    }
}
