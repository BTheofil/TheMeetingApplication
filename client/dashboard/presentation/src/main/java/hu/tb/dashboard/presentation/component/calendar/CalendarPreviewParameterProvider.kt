package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import hu.tb.dashboard.presentation.model.CalendarDay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

internal val PreviewMonth = YearMonth(2026, 9)
internal val PreviewToday = LocalDate(2026, 9, 1)
internal val PreviewSelectedDate = LocalDate(2026, 9, 3)

internal class CalendarDayPreviewParameterProvider : PreviewParameterProvider<CalendarDay> {
    override val values = sequenceOf(
        CalendarDay(PreviewToday),
        CalendarDay(PreviewToday, sessionCount = 2),
        CalendarDay(PreviewToday, hasOpenSlot = true),
        CalendarDay(PreviewToday, sessionCount = 5, hasOpenSlot = true)
    )
}