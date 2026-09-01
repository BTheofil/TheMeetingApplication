package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.DashboardState
import hu.tb.dashboard.presentation.ThemePreviews
import hu.tb.dashboard.presentation.model.OpenSlot
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.dashboard.presentation.util.currentDate
import hu.tb.design_system.theme.MeetingTheme
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.plus
import kotlinx.datetime.previousOrSame
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth

private const val WEEKS_IN_GRID = 6

@Composable
internal fun MonthGrid(
    modifier: Modifier = Modifier,
    currentMonth: YearMonth,
    state: DashboardState,
    onDateSelect: (LocalDate) -> Unit
) {
    val gridStart = currentMonth
        .firstDay
        .previousOrSame(WeekDays.first())

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(WEEKS_IN_GRID) { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(WeekDays.size) { dayIndex ->
                    val date = gridStart.plus(week * WeekDays.size + dayIndex, DateTimeUnit.DAY)
                    DayCell(
                        modifier = Modifier.weight(1f),
                        day = state.dayOf(date),
                        isSelected = date == state.selectedDate,
                        isToday = date == state.today,
                        isInVisibleMonth = date.yearMonth == currentMonth,
                        onClick = { onDateSelect(date) }
                    )
                }
            }
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

private fun monthGridPreviewState(): DashboardState {
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
private fun MonthGridPreview() {
    MeetingTheme {
        MonthGrid(
            state = monthGridPreviewState(),
            currentMonth = Clock.System.todayIn(TimeZone.currentSystemDefault()).yearMonth,
            onDateSelect = {}
        )
    }
}
