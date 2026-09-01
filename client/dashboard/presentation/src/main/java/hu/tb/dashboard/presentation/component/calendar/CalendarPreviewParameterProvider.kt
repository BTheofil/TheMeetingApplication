package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import hu.tb.dashboard.presentation.DashboardState
import hu.tb.dashboard.presentation.model.CalendarDay
import hu.tb.dashboard.presentation.model.CalendarMonth
import hu.tb.dashboard.presentation.model.CalendarWeek
import hu.tb.dashboard.presentation.model.OpenSlot
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.dashboard.presentation.model.buildCalendarMonth
import hu.tb.dashboard.presentation.model.buildCalendarWeek
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.YearMonth
import kotlinx.datetime.plus

internal val PreviewMonth = YearMonth(2026, 9)
internal val PreviewToday = LocalDate(2026, 9, 1)
internal val PreviewSelectedDate = LocalDate(2026, 9, 3)

internal class CalendarMonthPreviewParameterProvider : PreviewParameterProvider<CalendarMonth> {
    override val values = sequenceOf(
        calendarPreviewState().buildCalendarMonth(PreviewMonth),
        DashboardState(today = PreviewToday, selectedDate = PreviewSelectedDate)
            .buildCalendarMonth(PreviewMonth)
    )
}

internal class CalendarWeekPreviewParameterProvider : PreviewParameterProvider<CalendarWeek> {
    override val values = sequenceOf(
        calendarPreviewState().buildCalendarWeek(PreviewSelectedDate),
        DashboardState(today = PreviewToday, selectedDate = PreviewSelectedDate)
            .buildCalendarWeek(PreviewSelectedDate)
    )
}

internal class CalendarDayPreviewParameterProvider : PreviewParameterProvider<CalendarDay> {
    override val values = sequenceOf(
        CalendarDay(PreviewToday),
        CalendarDay(PreviewToday, sessionCount = 2),
        CalendarDay(PreviewToday, hasOpenSlot = true),
        CalendarDay(PreviewToday, sessionCount = 5, hasOpenSlot = true)
    )
}

internal fun calendarPreviewState(): DashboardState = DashboardState(
    today = PreviewToday,
    selectedDate = PreviewSelectedDate,
    sessions = listOf(
        previewSession(PreviewToday),
        previewSession(PreviewToday),
        previewSession(PreviewSelectedDate),
        previewSession(PreviewToday.plus(4, DateTimeUnit.DAY)),
        previewSession(PreviewToday.plus(4, DateTimeUnit.DAY)),
        previewSession(PreviewToday.plus(4, DateTimeUnit.DAY)),
        previewSession(PreviewToday.plus(4, DateTimeUnit.DAY)),
        previewSession(PreviewToday.plus(20, DateTimeUnit.DAY))
    ),
    openSlots = listOf(
        OpenSlot("coach-anna", PreviewToday, LocalTime(15, 0), 45),
        OpenSlot("coach-mark", PreviewToday.plus(2, DateTimeUnit.DAY), LocalTime(10, 0), 60),
        OpenSlot("coach-mark", PreviewToday.plus(4, DateTimeUnit.DAY), LocalTime(10, 0), 60),
        OpenSlot("coach-anna", PreviewToday.plus(12, DateTimeUnit.DAY), LocalTime(9, 0), 30)
    )
)

private fun previewSession(date: LocalDate) = SessionItem(
    id = "preview-$date-${date.day}",
    title = "Session",
    counterpartName = "Anna Kovacs",
    date = date,
    start = LocalTime(9, 0),
    durationMinutes = 60
)
