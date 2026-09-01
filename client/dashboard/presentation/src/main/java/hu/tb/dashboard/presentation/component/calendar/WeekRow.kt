package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import hu.tb.dashboard.presentation.model.CalendarWeek
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.LocalDate

@Composable
internal fun WeekRow(
    week: CalendarWeek,
    selectedDate: LocalDate,
    today: LocalDate,
    modifier: Modifier = Modifier,
    onDateSelect: (LocalDate) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth()) {
        week.days.forEach { day ->
            DayCell(
                modifier = Modifier.weight(1f),
                day = day,
                isSelected = day.date == selectedDate,
                isToday = day.date == today,
                onClick = { onDateSelect(day.date) }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun WeekRowPreview(
    @PreviewParameter(CalendarWeekPreviewParameterProvider::class) week: CalendarWeek
) {
    MeetingTheme {
        WeekRow(
            week = week,
            selectedDate = PreviewSelectedDate,
            today = PreviewToday,
            onDateSelect = {}
        )
    }
}
