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
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.dashboard.presentation.DashboardAction
import hu.tb.dashboard.presentation.component.common.AvailableRing
import hu.tb.dashboard.presentation.component.common.BookedDot
import hu.tb.dashboard.presentation.component.common.DashboardCard
import hu.tb.dashboard.presentation.model.OpenSlot
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.dashboard.presentation.model.buildCalendarMonth
import hu.tb.dashboard.presentation.model.buildCalendarWeek
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth
import kotlinx.datetime.yearMonth

@TraceRecomposition
@Composable
internal fun CollapsibleCalendar(
    modifier: Modifier = Modifier,
    sessions: List<SessionItem>,
    openSlots: List<OpenSlot>,
    todayDate: LocalDate,
    selectedDate: LocalDate,
    action: (DashboardAction) -> Unit
) {
    var isCalendarExpanded by remember { mutableStateOf(false) }
    var currentMonth by remember { mutableStateOf(todayDate.yearMonth) }

    val month = remember(sessions, openSlots, currentMonth) {
        buildCalendarMonth(currentMonth, sessions, openSlots)
    }
    val week = remember(sessions, openSlots, selectedDate) {
        buildCalendarWeek(selectedDate, sessions, openSlots)
    }
    val onDateSelect: (LocalDate) -> Unit = remember(action) {
        { date -> action(DashboardAction.OnDateSelect(date)) }
    }

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
                onNextMonth = { currentMonth = currentMonth.plusMonth() },
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
                        month = month,
                        selectedDate = selectedDate,
                        today = todayDate,
                        visibleMonth = currentMonth,
                        onDateSelect = onDateSelect
                    )
                } else {
                    WeekRow(
                        week = week,
                        selectedDate = selectedDate,
                        today = todayDate,
                        onDateSelect = onDateSelect
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

@PreviewLightDark
@Composable
private fun CollapsibleCalendarPreview() {
    MeetingTheme {
        CollapsibleCalendar(
            sessions = emptyList(),
            openSlots = emptyList(),
            todayDate = LocalDate(2024, Month.APRIL, 16),
            selectedDate = LocalDate(2024, Month.APRIL, 16),
            action = {})
    }
}
