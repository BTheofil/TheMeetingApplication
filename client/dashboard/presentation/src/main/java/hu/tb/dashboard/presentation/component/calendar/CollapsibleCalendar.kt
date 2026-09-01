package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.DashboardAction
import hu.tb.dashboard.presentation.DashboardState
import hu.tb.dashboard.presentation.component.common.AvailableRing
import hu.tb.dashboard.presentation.component.common.BookedDot
import hu.tb.dashboard.presentation.component.common.DashboardCard
import hu.tb.dashboard.presentation.model.OpenSlot
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.dashboard.presentation.util.currentDate
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plus
import kotlinx.datetime.plusMonth
import kotlinx.datetime.yearMonth

@Composable
internal fun CollapsibleCalendar(
    modifier: Modifier = Modifier,
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    var isCalendarExpanded by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(state.today.yearMonth) }

    DashboardCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalendarHeader(
                modifier = Modifier.padding(start = 8.dp),
                visibleMonth = currentMonth,
                isExpanded = isCalendarExpanded,
                onPreviousMonth = { currentMonth = currentMonth.minusMonth() },
                onNextMonth = { currentMonth = currentMonth.plusMonth()  },
                onToggleExpanded = { isCalendarExpanded = !isCalendarExpanded }
            )
            WeekdayLabels()
            AnimatedContent(
                targetState = isCalendarExpanded,
                transitionSpec = {
                    (fadeIn() + expandVertically()) togetherWith (fadeOut() + shrinkVertically())
                },
                label = "calendarMode"
            ) { expanded ->
                if (expanded) {
                    MonthGrid(
                        modifier = Modifier.fillMaxWidth(),
                        currentMonth = currentMonth,
                        state = state,
                        onDateSelect = { action(DashboardAction.OnDateSelect(it)) }
                    )
                } else {
                    WeekRow(
                        state = state,
                        onDateSelect = { action(DashboardAction.OnDateSelect(it)) }
                    )
                }
            }
            CalendarLegend(modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun CalendarLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(label = "Booked") { BookedDot() }
        LegendItem(label = "Free hours") { AvailableRing() }
    }
}

@Composable
private fun LegendItem(
    label: String,
    indicator: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        indicator()
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun previewSession(date: LocalDate) = SessionItem(
    id = "preview-$date",
    title = "Session",
    counterpartName = "Anna Kovács",
    date = date,
    start = LocalTime(9, 0),
    durationMinutes = 60
)

private fun collapsibleCalendarPreviewState(): DashboardState {
    val today = currentDate()
    return DashboardState(
        today = today,
        selectedDate = today,
        sessions = listOf(
            previewSession(today),
            previewSession(today),
            previewSession(today.plus(1, DateTimeUnit.DAY))
        ),
        openSlots = listOf(
            OpenSlot("o1", "coach-anna", today, LocalTime(15, 0), 45),
            OpenSlot("o2", "coach-mark", today.plus(3, DateTimeUnit.DAY), LocalTime(10, 0), 60)
        )
    )
}

@PreviewLightDark
@Composable
private fun CollapsibleCalendarPreview() {
    MeetingTheme {
        CollapsibleCalendar(state = collapsibleCalendarPreviewState(), action = {})
    }
}
