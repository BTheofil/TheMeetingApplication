package hu.tb.design_system.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.component.calendar.model.CalendarDay
import hu.tb.design_system.component.calendar.model.CalendarMonth
import hu.tb.design_system.component.calendar.model.CalendarWeek
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

@Stable
data class MonthGridParameter(
    val selectedDate: LocalDate,
    val today: LocalDate,
    val visibleMonth: YearMonth
)

@TraceRecomposition
@Composable
fun MonthGrid(
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
            WeekRow(
                week = week,
                weekRowParameter = WeekRowParameter(
                    selectedDate = monthGridParameter.selectedDate,
                    today = monthGridParameter.today,
                    visibleMonth = monthGridParameter.visibleMonth
                ),
                onDateSelect = onDateSelect
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MonthGridPreview() {
    MeetingTheme {
        MonthGrid(
            month = CalendarMonth(
                weeks = listOf(
                    CalendarWeek(
                        days = listOf(
                            CalendarDay(date = LocalDate(2026, 9, 3)),
                            CalendarDay(date = LocalDate(2026, 9, 4)),
                            CalendarDay(date = LocalDate(2026, 9, 5)),
                            CalendarDay(date = LocalDate(2026, 9, 6)),
                            CalendarDay(date = LocalDate(2026, 9, 7)),
                            CalendarDay(date = LocalDate(2026, 9, 8)),
                            CalendarDay(date = LocalDate(2026, 9, 9)),
                        )
                    )
                )
            ),
            monthGridParameter = MonthGridParameter(
                selectedDate = LocalDate(2026, 9, 3),
                today = LocalDate(2026, 9, 1),
                visibleMonth = YearMonth(2026, 9)
            ),
            onDateSelect = {}
        )
    }
}
