package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.model.CalendarMonth
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth

@Composable
internal fun MonthGrid(
    month: CalendarMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    visibleMonth: YearMonth,
    modifier: Modifier = Modifier,
    onDateSelect: (LocalDate) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        month.weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.days.forEach { day ->
                    DayCell(
                        modifier = Modifier.weight(1f),
                        day = day,
                        isSelected = day.date == selectedDate,
                        isToday = day.date == today,
                        isInVisibleMonth = day.date.yearMonth == visibleMonth,
                        onClick = { onDateSelect(day.date) }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MonthGridPreview() {
    MeetingTheme {
        MonthGrid(
            month = CalendarMonth(weeks = emptyList()),
            selectedDate = PreviewSelectedDate,
            today = PreviewToday,
            visibleMonth = PreviewMonth,
            onDateSelect = {}
        )
    }
}
