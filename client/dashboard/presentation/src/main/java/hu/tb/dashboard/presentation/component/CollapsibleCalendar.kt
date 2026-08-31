package hu.tb.dashboard.presentation.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.DashboardAction
import hu.tb.dashboard.presentation.DashboardState
import hu.tb.dashboard.presentation.SampleData
import hu.tb.design_system.theme.MeetingTheme

@Composable
internal fun CollapsibleCalendar(
    state: DashboardState,
    modifier: Modifier = Modifier,
    action: (DashboardAction) -> Unit
) {
    var isCalendarExpanded by remember { mutableStateOf(false) }

    DashboardCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalendarHeader(
                modifier = Modifier.padding(start = 8.dp),
                visibleMonth = state.visibleMonth,
                isExpanded = isCalendarExpanded,
                onPreviousMonth = { action(DashboardAction.OnPreviousMonth) },
                onNextMonth = { action(DashboardAction.OnNextMonth) },
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

@Preview(showBackground = true)
@Composable
private fun CollapsibleCalendarPreview() {
    MeetingTheme {
        CollapsibleCalendar(state = SampleData.clientState(), action = {})
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CollapsibleCalendarExpandedPreview() {
    MeetingTheme {
        CollapsibleCalendar(
            state = SampleData.clientState(),
            action = {}
        )
    }
}
