package hu.tb.schedule.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.component.calendar.CalendarHeader
import hu.tb.design_system.component.calendar.MonthGrid
import hu.tb.design_system.component.calendar.MonthGridParameter
import hu.tb.design_system.component.calendar.WeekdayLabels
import hu.tb.design_system.component.calendar.model.CalendarMonth
import hu.tb.design_system.component.calendar.model.buildCalendarMonth
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.schedule.domain.ScheduleCalendarInfo
import kotlinx.datetime.LocalDate
import kotlinx.datetime.yearMonth

@TraceRecomposition
@Composable
internal fun ScheduleCalendar(
    month: CalendarMonth,
    scheduleCalendarInfo: ScheduleCalendarInfo,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelect: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalendarHeader(
            modifier = Modifier.padding(start = 12.dp),
            visibleMonth = scheduleCalendarInfo.visibleMonth,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth
        )
        WeekdayLabels()
        MonthGrid(
            month = month,
            monthGridParameter = MonthGridParameter(
                selectedDate = scheduleCalendarInfo.selectedDate,
                today = scheduleCalendarInfo.today,
                visibleMonth = scheduleCalendarInfo.visibleMonth
            ),
            onDateSelect = onDateSelect
        )
    }
}

@PreviewLightDark
@Composable
private fun ScheduleCalendarPreview() {
    val today = LocalDate(2026, 9, 14)
    MeetingTheme {
        ScheduleCalendar(
            month = buildCalendarMonth(today.yearMonth),
            scheduleCalendarInfo = ScheduleCalendarInfo(
                visibleMonth = today.yearMonth,
                selectedDate = today,
                today = today,
            ),
            onPreviousMonth = {},
            onNextMonth = {},
            onDateSelect = {}
        )
    }
}
