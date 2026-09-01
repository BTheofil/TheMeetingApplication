package hu.tb.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.dashboard.presentation.component.OpenSlotCard
import hu.tb.dashboard.presentation.component.SessionCard
import hu.tb.dashboard.presentation.component.calendar.CollapsibleCalendar
import hu.tb.dashboard.presentation.component.coach.CoachOpenHoursCard
import hu.tb.dashboard.presentation.component.coach.DiscoverCoachesCard
import hu.tb.dashboard.presentation.component.coach.MyCoachesSection
import hu.tb.dashboard.presentation.component.common.DashboardCard
import hu.tb.dashboard.presentation.component.common.SectionHeader
import hu.tb.dashboard.presentation.model.CoachItem
import hu.tb.dashboard.presentation.model.OpenSlot
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.dashboard.presentation.util.currentDate
import hu.tb.dashboard.presentation.util.formatSectionLabel
import hu.tb.design_system.Icons
import hu.tb.design_system.modifier.authGlowBackground
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.domain.ProfileType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    navigationRequest: (NavigationRequest) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        action = { dashboardAction ->
            when (dashboardAction) {
                is DashboardAction.OnDateSelect -> viewModel.onDateSelected(dashboardAction.date)
                is NavigationRequest -> navigationRequest(dashboardAction)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
private fun DashboardScreen(
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .authGlowBackground()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
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
                CollapsibleCalendar(state = state, action = action)
                SelectedDayBooked(state = state, action = action)
                SelectedDayOpenHours(state = state, action = action)
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
        ProfileType.COACH -> CoachOpenHoursCard(
            onCreateOpenHours = { action(DashboardAction.OnCreateOpenHoursClick) }
        )

        ProfileType.NORMAL -> Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            MyCoachesSection(
                coaches = state.coaches,
                onCoachClick = { action(DashboardAction.OnCoachClick(it)) }
            )
            DiscoverCoachesCard(
                hasCoaches = state.coaches.isNotEmpty(),
                onDiscoverCoaches = { action(DashboardAction.OnDiscoverCoachesClick) }
            )
        }
    }
}

@Composable
private fun SelectedDayBooked(
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    val sessions = state.sessionsOn(state.selectedDate)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = 4.dp),
            title = "Booked · ${state.selectedDate.formatSectionLabel(state.today)}"
        )
        if (sessions.isEmpty()) {
            DashboardCard(modifier = Modifier.fillMaxWidth()) {
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
                    session = session,
                    onClick = { action(DashboardAction.OnSessionClick(session.id)) }
                )
            }
        }
    }
}

@Composable
private fun SelectedDayOpenHours(
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    val slots = state.openSlotsOn(state.selectedDate)
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
                onClick = {
                    action(
                        when (state.profileType) {
                            ProfileType.COACH -> DashboardAction.OnCreateOpenHoursClick
                            ProfileType.NORMAL -> DashboardAction.OnCoachClick(slot.coachId)
                        }
                    )
                }
            )
        }
    }
}

private val previewCoaches = listOf(
    CoachItem(id = "coach-anna", name = "Anna Kovács", openHourCount = 3),
    CoachItem(id = "coach-mark", name = "Márk Szabó", openHourCount = 4),
    CoachItem(id = "coach-julia", name = "Júlia Papp", openHourCount = 0)
)

private fun previewState(
    profileType: ProfileType,
    coaches: List<CoachItem> = previewCoaches
): DashboardState {
    val today = currentDate()
    val sessions = listOf(
        SessionItem(
            id = "s1",
            title = "Leg day",
            counterpartName = "Anna Kovács",
            date = today,
            start = LocalTime(9, 0),
            durationMinutes = 60,
            isNext = true
        ),
        SessionItem(
            id = "s2",
            title = "Mobility",
            counterpartName = "Júlia Papp",
            date = today,
            start = LocalTime(17, 30),
            durationMinutes = 45
        ),
        SessionItem(
            id = "s3",
            title = "Cardio intervals",
            counterpartName = "Márk Szabó",
            date = today.plus(1, DateTimeUnit.DAY),
            start = LocalTime(18, 30),
            durationMinutes = 45
        )
    )
    val slots = listOf(
        OpenSlot("o1", "coach-anna", today, LocalTime(15, 0), 45),
        OpenSlot("o2", "coach-mark", today.plus(2, DateTimeUnit.DAY), LocalTime(10, 0), 60)
    )

    return DashboardState(
        profileType = profileType,
        today = today,
        selectedDate = today,
        sessions = sessions,
        openSlots = slots,
        coaches = coaches
    )
}

@ThemePreviews
@Composable
private fun DashboardScreenCoachPreview() {
    MeetingTheme {
        DashboardScreen(
            state = previewState(ProfileType.COACH, coaches = emptyList()),
            action = {}
        )
    }
}

@ThemePreviews
@Composable
private fun DashboardScreenClientPreview() {
    MeetingTheme {
        DashboardScreen(state = previewState(ProfileType.NORMAL), action = {})
    }
}

@ThemePreviews
@Composable
private fun DashboardScreenNoCoachesPreview() {
    MeetingTheme {
        DashboardScreen(
            state = previewState(ProfileType.NORMAL, coaches = emptyList()),
            action = {}
        )
    }
}
