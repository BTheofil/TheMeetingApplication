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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.dashboard.domain.OpenSlot
import hu.tb.dashboard.domain.SessionItem
import hu.tb.dashboard.presentation.DashboardAction
import hu.tb.dashboard.presentation.component.calendar.model.buildCalendarMonth
import hu.tb.dashboard.presentation.component.calendar.model.buildCalendarWeek
import hu.tb.design_system.component.AvailableRing
import hu.tb.design_system.component.BookedDot
import hu.tb.design_system.component.CardComponent
import hu.tb.design_system.component.calendar.CalendarHeader
import hu.tb.design_system.component.calendar.MonthGrid
import hu.tb.design_system.component.calendar.MonthGridParameter
import hu.tb.design_system.component.calendar.WeekRow
import hu.tb.design_system.component.calendar.WeekRowParameter
import hu.tb.design_system.component.calendar.WeekdayLabels
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth
import kotlinx.datetime.yearMonth

@Stable
data class CollapsibleCalendarParameter(
    val sessions: List<SessionItem>,
    val openSlots: List<OpenSlot>,
    val todayDate: LocalDate,
    val selectedDate: LocalDate
)

@TraceRecomposition
@Composable
internal fun CollapsibleCalendar(
    calendarParameter: CollapsibleCalendarParameter,
    action: (DashboardAction) -> Unit
) {
    var isCalendarExpanded by remember { mutableStateOf(true) }
    var currentMonth by remember { mutableStateOf(calendarParameter.todayDate.yearMonth) }

    val month = remember(calendarParameter.sessions, calendarParameter.openSlots, currentMonth) {
        buildCalendarMonth(currentMonth, calendarParameter.sessions, calendarParameter.openSlots)
    }
    val week = remember(
        calendarParameter.sessions,
        calendarParameter.openSlots,
        calendarParameter.selectedDate
    ) {
        buildCalendarWeek(
            calendarParameter.selectedDate,
            calendarParameter.sessions,
            calendarParameter.openSlots
        )
    }
    val onDateSelect: (LocalDate) -> Unit = remember(action) {
        { date -> action(DashboardAction.OnDateSelect(date)) }
    }

    CardComponent {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalendarHeader(
                modifier = Modifier.padding(start = 12.dp),
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
                        monthGridParameter = MonthGridParameter(
                            calendarParameter.selectedDate,
                            calendarParameter.todayDate,
                            currentMonth
                        ),
                        onDateSelect = onDateSelect
                    )
                } else {
                    WeekRow(
                        week = week,
                        weekRowParameter = WeekRowParameter(
                            selectedDate = calendarParameter.selectedDate,
                            today = calendarParameter.todayDate,
                            visibleMonth = currentMonth
                        ),
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
            calendarParameter = CollapsibleCalendarParameter(
                sessions = emptyList(),
                openSlots = emptyList(),
                todayDate = LocalDate(2026, 1, 1),
                selectedDate = LocalDate(2026, 1, 2)
            ),
            action = {})
    }
}
