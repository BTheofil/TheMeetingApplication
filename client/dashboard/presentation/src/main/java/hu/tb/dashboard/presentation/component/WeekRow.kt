package hu.tb.dashboard.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import hu.tb.dashboard.presentation.DashboardState
import hu.tb.dashboard.presentation.SampleData
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.previousOrSame

@Composable
internal fun WeekRow(
    state: DashboardState,
    modifier: Modifier = Modifier,
    onDateSelect: (LocalDate) -> Unit
) {
    val firstDayOfWeek = state.selectedDate
        .previousOrSame(WeekDays.first())

    Row(modifier = modifier.fillMaxWidth()) {
        repeat(WeekDays.size) { index ->
            val date = firstDayOfWeek.plus(index, DateTimeUnit.DAY)
            DayCell(
                modifier = Modifier.weight(1f),
                day = state.dayOf(date),
                isSelected = date == state.selectedDate,
                isToday = date == state.today,
                onClick = { onDateSelect(date) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekRowPreview() {
    MeetingTheme {
        WeekRow(state = SampleData.clientState(), onDateSelect = {})
    }
}
