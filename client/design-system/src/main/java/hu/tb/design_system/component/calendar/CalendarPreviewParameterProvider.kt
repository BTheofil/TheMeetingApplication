package hu.tb.design_system.component.calendar

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import hu.tb.design_system.component.calendar.model.CalendarDay
import kotlinx.datetime.LocalDate

internal class CalendarDayPreviewParameterProvider : PreviewParameterProvider<CalendarDay> {
    override val values = sequenceOf(
        CalendarDay(LocalDate(2026, 9, 1)),
        CalendarDay(LocalDate(2026, 9, 2), sessionCount = 2),
        CalendarDay(LocalDate(2026, 9, 3), hasOpenSlot = true),
        CalendarDay(LocalDate(2026, 9, 4), sessionCount = 5, hasOpenSlot = true)
    )
}