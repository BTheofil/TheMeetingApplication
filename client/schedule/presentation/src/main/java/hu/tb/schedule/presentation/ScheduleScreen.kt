package hu.tb.schedule.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.component.calendar.model.CalendarDay
import hu.tb.design_system.component.calendar.model.buildCalendarMonth
import hu.tb.design_system.modifier.glowBackground
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.schedule.domain.ScheduleCalendarInfo
import hu.tb.schedule.domain.SlotListInfo
import hu.tb.schedule.domain.TimeSlot
import hu.tb.schedule.presentation.component.ScheduleCalendar
import hu.tb.schedule.presentation.component.ScheduleCard
import hu.tb.schedule.presentation.component.SlotList
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Clock

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel(),
    navigationRequest: () -> Unit
) {
    ScheduleScreen(
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
    state: ScheduleState,
    action: (ScheduleAction) -> Unit
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val month = remember(state.visibleMonth, state.slotsByDate, state.drafts) {
        buildCalendarMonth(state.visibleMonth) { date ->
            CalendarDay(
                date = date, hasOpenSlot = !state.slotsByDate[date].isNullOrEmpty() ||
                        !state.drafts[date].isNullOrEmpty()
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .glowBackground()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
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
                ScheduleCard {
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
                ScheduleCard {
                    SlotList(
                        slotListInfo = SlotListInfo(
                            date = state.selectedDate,
                            slots = state.slotsByDate[state.selectedDate].orEmpty(),
                            drafts = state.drafts[state.selectedDate].orEmpty(),
                        ),
                        isPasteEnabled = state.clipboard.isNotEmpty(),
                        onCopyDay = { action(ScheduleAction.DayCopy(state.selectedDate)) },
                        onPasteDay = { action(ScheduleAction.DayPaste(state.selectedDate)) },
                        onCopySlot = { action(ScheduleAction.SlotCopy(it)) },
                        onDeleteSlot = {
                            action(ScheduleAction.SlotDelete(state.selectedDate, it))
                        },
                        onDeleteDraft = {
                            action(ScheduleAction.DraftDelete(state.selectedDate, it))
                        },
                        onDraftConfirm = {
                            action(ScheduleAction.DraftConfirm(state.selectedDate, it))
                        }
                    )
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { action(ScheduleAction.PublishDrafts) },
                    enabled = state.drafts.isNotEmpty(),
                    content = {
                        Text(
                            text = "Publish",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
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
            state = ScheduleState(
                slotsByDate = mapOf(
                    today to listOf(
                        TimeSlot(1, LocalTime(9, 0), LocalTime(10, 0)),
                        TimeSlot(2, LocalTime(11, 0), LocalTime(11, 30))
                    )
                )
            ),
            action = {}
        )
    }
}