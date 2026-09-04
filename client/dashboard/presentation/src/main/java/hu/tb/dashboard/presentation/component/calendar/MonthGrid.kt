package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.dashboard.presentation.model.CalendarMonth
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth

@Stable
data class MonthGridParameter(
    val selectedDate: LocalDate,
    val today: LocalDate,
    val visibleMonth: YearMonth
)

@TraceRecomposition
@Composable
internal fun MonthGrid(
    month: CalendarMonth,
    monthGridParameter: MonthGridParameter,
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
                        isSelected = day.date == monthGridParameter.selectedDate,
                        isToday = day.date == monthGridParameter.today,
                        isInVisibleMonth = day.date.yearMonth == monthGridParameter.visibleMonth,
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
            monthGridParameter = MonthGridParameter(
                selectedDate = PreviewSelectedDate,
                today = PreviewToday,
                visibleMonth = PreviewMonth
            ),
            onDateSelect = {}
        )
    }
}
