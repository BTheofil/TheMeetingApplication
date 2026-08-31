package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.DashboardState
import hu.tb.dashboard.presentation.preview.SampleData
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.previousOrSame
import kotlinx.datetime.yearMonth

private const val WEEKS_IN_GRID = 6

@Composable
internal fun MonthGrid(
    state: DashboardState,
    modifier: Modifier = Modifier,
    onDateSelect: (LocalDate) -> Unit
) {
    val gridStart = state.visibleMonth
        .firstDay
        .previousOrSame(WeekDays.first())

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(WEEKS_IN_GRID) { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(WeekDays.size) { dayIndex ->
                    val date = gridStart.plus(week * WeekDays.size + dayIndex, DateTimeUnit.DAY)
                    DayCell(
                        modifier = Modifier.weight(1f),
                        day = state.dayOf(date),
                        isSelected = date == state.selectedDate,
                        isToday = date == state.today,
                        isInVisibleMonth = date.yearMonth == state.visibleMonth,
                        onClick = { onDateSelect(date) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthGridPreview() {
    MeetingTheme {
        MonthGrid(
            state = SampleData.clientState(),
            onDateSelect = {}
        )
    }
}
