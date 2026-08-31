package hu.tb.dashboard.presentation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.dashboard.presentation.component.CoachOpenHoursCard
import hu.tb.dashboard.presentation.component.CollapsibleCalendar
import hu.tb.dashboard.presentation.component.DashboardCard
import hu.tb.dashboard.presentation.component.MyCoachesSection
import hu.tb.dashboard.presentation.component.OpenSlotCard
import hu.tb.dashboard.presentation.component.SectionHeader
import hu.tb.dashboard.presentation.component.SessionCard
import hu.tb.design_system.Icons
import hu.tb.design_system.modifier.authGlowBackground
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.domain.ProfileType
import java.time.YearMonth

@Composable
fun DashboardScreen(
    onProfileClick: () -> Unit
) {
    var state by remember { mutableStateOf(SampleData.clientState()) }

    DashboardScreen(
        state = state,
        action = { dashboardAction ->
            when (dashboardAction) {
                DashboardAction.OnProfileClick -> onProfileClick()

                is DashboardAction.OnDateSelect -> state = SampleData.stateOf(
                    profileType = state.profileType,
                    sessions = SampleData.clientSessions(),
                    slots = SampleData.openSlots(),
                    coaches = state.coaches,
                    selectedDate = dashboardAction.date
                )

                DashboardAction.OnPreviousMonth ->
                    state = state.copy(visibleMonth = state.visibleMonth.minusMonths(1))

                DashboardAction.OnNextMonth ->
                    state = state.copy(visibleMonth = state.visibleMonth.plusMonths(1))

                // TODO(nav): open the session detail screen
                is DashboardAction.OnSessionClick -> Unit
                // TODO(nav): navigate to the open-hours editor of the coach
                DashboardAction.OnCreateOpenHoursClick -> Unit
                // TODO(nav): open the coach availability screen and reserve there
                is DashboardAction.OnCoachClick -> Unit
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
            openSlotCount = state.availableSlots.count {
                YearMonth.from(it.date) == state.visibleMonth
            },
            onCreateOpenHours = { action(DashboardAction.OnCreateOpenHoursClick) }
        )

        ProfileType.NORMAL -> MyCoachesSection(
            coaches = state.coaches,
            onCoachClick = { action(DashboardAction.OnCoachClick(it)) }
        )
    }
}

@Composable
private fun SelectedDayBooked(
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = 4.dp),
            title = "Booked · ${state.selectedDate.formatSectionLabel(state.today)}"
        )
        if (state.sessionsOnSelectedDay.isEmpty()) {
            DashboardCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(20.dp),
                    text = "No sessions on this day.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            state.sessionsOnSelectedDay.forEach { session ->
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
    if (state.openSlotsOnSelectedDay.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = 4.dp),
            title = "Free hours"
        )
        state.openSlotsOnSelectedDay.forEach { slot ->
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

@Preview(showBackground = true)
@Composable
private fun DashboardScreenCoachPreview() {
    MeetingTheme {
        DashboardScreen(state = SampleData.coachState(), action = {})
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DashboardScreenCoachDarkPreview() {
    MeetingTheme {
        DashboardScreen(state = SampleData.coachState(), action = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenClientPreview() {
    MeetingTheme {
        DashboardScreen(state = SampleData.clientState(), action = {})
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DashboardScreenClientDarkPreview() {
    MeetingTheme {
        DashboardScreen(state = SampleData.clientState(), action = {})
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DashboardScreenNoCoachesPreview() {
    MeetingTheme {
        DashboardScreen(state = SampleData.clientStateNoCoaches(), action = {})
    }
}
