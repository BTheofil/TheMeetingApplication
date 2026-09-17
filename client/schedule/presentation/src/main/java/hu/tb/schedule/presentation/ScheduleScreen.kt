package hu.tb.schedule.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.component.CardComponent
import hu.tb.design_system.component.CountdownSnackbar
import hu.tb.design_system.component.CountdownSnackbarVisuals
import hu.tb.design_system.component.calendar.model.CalendarDay
import hu.tb.design_system.component.calendar.model.buildCalendarMonth
import hu.tb.design_system.modifier.glowBackground
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.schedule.domain.ScheduleCalendarInfo
import hu.tb.schedule.domain.SlotListInfo
import hu.tb.schedule.domain.TimeRange
import hu.tb.schedule.presentation.component.ScheduleCalendar
import hu.tb.schedule.presentation.component.SessionsPanel
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.isNullOrEmpty
import kotlin.collections.orEmpty
import kotlin.time.Clock

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel(),
    navigationRequest: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            snackbarHostState.showSnackbar(
                visuals = CountdownSnackbarVisuals(message = it)
            )
        }
    }

    ScheduleScreen(
        snackbarHostState = snackbarHostState,
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = {
            if (it is ScheduleAction.BackRequest) navigationRequest()
            else viewModel.action(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
private fun ScheduleScreen(
    snackbarHostState: SnackbarHostState,
    state: ScheduleState,
    action: (ScheduleAction) -> Unit
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val month = remember(state.visibleMonth, state.sessionsByDate, state.draftsByDate) {
        buildCalendarMonth(state.visibleMonth) { date ->
            CalendarDay(
                date = date, hasOpenSlot = !state.sessionsByDate[date].isNullOrEmpty() ||
                        !state.draftsByDate[date].isNullOrEmpty()
            )
        }
    }
    val slotListInfo = remember(state.selectedDate, state.sessionsByDate, state.draftsByDate) {
        SlotListInfo(
            date = state.selectedDate,
            sessions = state.sessionsByDate[state.selectedDate].orEmpty(),
            drafts = state.draftsByDate[state.selectedDate].orEmpty()
        )
    }
    var clipboard by remember { mutableStateOf(emptyList<TimeRange>()) }

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
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { action(ScheduleAction.BackRequest) }) {
                            Icon(
                                painter = painterResource(Icons.arrow_back),
                                contentDescription = "Back",
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
                    .screenPadding()
                    .padding(horizontal = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CardComponent {
                    ScheduleCalendar(
                        month = month,
                        scheduleCalendarInfo = ScheduleCalendarInfo(
                            visibleMonth = state.visibleMonth,
                            selectedDate = state.selectedDate,
                            today = today,
                        ),
                        onPreviousMonth = { action(ScheduleAction.PreviousMonth) },
                        onNextMonth = { action(ScheduleAction.NextMonth) },
                        onDateSelect = { action(ScheduleAction.DateSelect(it)) }
                    )
                    if (state.isCalendarLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
                CardComponent {
                    SessionsPanel(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        slotListInfo = slotListInfo,
                        isPasteEnabled = clipboard.isNotEmpty(),
                        isPastDay = slotListInfo.date < today,
                        deletingSessionIds = state.deletingSessionIds,
                        onCopyDay = { clipboard = slotListInfo.sessions + slotListInfo.drafts },
                        onPasteDay = {
                            action(ScheduleAction.DayPaste(state.selectedDate, clipboard))
                        },
                        onDeleteSession = {
                            action(ScheduleAction.SessionDelete(state.selectedDate, it))
                        },
                        onDeleteDraft = {
                            action(ScheduleAction.DraftDelete(state.selectedDate, it))
                        },
                        onCopy = { clipboard = listOf(it) },
                        onConfirmDraft = {
                            action(ScheduleAction.DraftConfirm(state.selectedDate, it))
                        }
                    )
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { action(ScheduleAction.DraftsPublish) },
                    enabled = state.draftsByDate.isNotEmpty() && !state.isPublishing,
                    content = {
                        if (state.isPublishing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Publish",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ScheduleScreenPreview() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    MeetingTheme {
        ScheduleScreen(
            snackbarHostState = SnackbarHostState(),
            state = ScheduleState(
                sessionsByDate = mapOf(
                    today to listOf(
                        TimeRange.SessionTime(1, LocalTime(9, 0), LocalTime(10, 0)),
                        TimeRange.SessionTime(2, LocalTime(11, 0), LocalTime(11, 30))
                    )
                )
            ),
            action = {}
        )
    }
}