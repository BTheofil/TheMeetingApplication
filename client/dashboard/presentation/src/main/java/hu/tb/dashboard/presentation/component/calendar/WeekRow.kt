package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hu.tb.dashboard.presentation.DashboardState
import hu.tb.dashboard.presentation.ThemePreviews
import hu.tb.dashboard.presentation.model.OpenSlot
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.dashboard.presentation.util.currentDate
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
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

private fun previewSession(date: LocalDate) = SessionItem(
    id = "preview-$date",
    title = "Session",
    counterpartName = "Anna Kovács",
    date = date,
    start = LocalTime(9, 0),
    durationMinutes = 60
)

private fun weekRowPreviewState(): DashboardState {
    val today = currentDate()
    return DashboardState(
        today = today,
        selectedDate = today,
        sessions = listOf(
            previewSession(today),
            previewSession(today),
            previewSession(today.plus(1, DateTimeUnit.DAY))
        ),
        openSlots = listOf(
            OpenSlot("o1", "coach-anna", today, LocalTime(15, 0), 45),
            OpenSlot("o2", "coach-mark", today.plus(3, DateTimeUnit.DAY), LocalTime(10, 0), 60)
        )
    )
}

@ThemePreviews
@Composable
private fun WeekRowPreview() {
    MeetingTheme {
        WeekRow(state = weekRowPreviewState(), onDateSelect = {})
    }
}
