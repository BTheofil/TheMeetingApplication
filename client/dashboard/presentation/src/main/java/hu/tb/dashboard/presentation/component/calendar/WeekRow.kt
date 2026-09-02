package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.dashboard.presentation.model.CalendarDay
import hu.tb.dashboard.presentation.model.CalendarWeek
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.LocalDate

@Stable
data class WeekRowParameter(
    val selectedDate: LocalDate,
    val today: LocalDate
)

@TraceRecomposition
@Composable
internal fun WeekRow(
    modifier: Modifier = Modifier,
    week: CalendarWeek,
    weekRowParameter: WeekRowParameter,
    onDateSelect: (LocalDate) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth()) {
        week.days.forEach { day ->
            DayCell(
                modifier = Modifier.weight(1f),
                day = day,
                isSelected = day.date == weekRowParameter.selectedDate,
                isToday = day.date == weekRowParameter.today,
                onClick = { onDateSelect(day.date) }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun WeekRowPreview() {
    MeetingTheme {
        WeekRow(
            week = CalendarWeek(
                days = listOf(
                    CalendarDay(
                        date = LocalDate(2026, 8, 1),
                        sessionCount = 3,
                        hasOpenSlot = true
                    ),
                    CalendarDay(
                        date = LocalDate(2026, 8, 2),
                        sessionCount = 1,
                    ),
                    CalendarDay(
                        date = LocalDate(2026, 8, 3),
                    ), CalendarDay(
                        date = LocalDate(2026, 8, 4),
                    ),
                    CalendarDay(
                        date = LocalDate(2026, 8, 5),
                    ),
                    CalendarDay(
                        date = LocalDate(2026, 8, 6),
                    ),
                    CalendarDay(
                        date = LocalDate(2026, 8, 7),
                    )
                )
            ),
            weekRowParameter = WeekRowParameter(
                selectedDate = PreviewSelectedDate,
                today = PreviewToday
            ),
            onDateSelect = {}
        )
    }
}
