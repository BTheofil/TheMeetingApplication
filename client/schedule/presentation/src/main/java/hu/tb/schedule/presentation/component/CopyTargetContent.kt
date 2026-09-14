package hu.tb.schedule.presentation.component

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import hu.tb.design_system.component.calendar.model.CalendarMonth
import hu.tb.design_system.component.calendar.model.buildCalendarMonth
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.schedule.domain.ScheduleCalendarInfo
import kotlinx.datetime.LocalDate
import kotlinx.datetime.yearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopyTargetDialog(
    sourceLabel: String,
    month: CalendarMonth,
    scheduleCalendarInfo: ScheduleCalendarInfo,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = onDismiss,
        content = {
            ScheduleDialog(
                content = {
                    Text(
                        text = "Copy $sourceLabel to",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    ScheduleCalendar(
                        month = month,
                        scheduleCalendarInfo = scheduleCalendarInfo,
                        onPreviousMonth = onPreviousMonth,
                        onNextMonth = onNextMonth,
                        onDateSelect = onDateSelect
                    )
                },
                onConfirm = onConfirm,
                onDismiss = onDismiss
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun CopyTargetDialogPreview() {
    val today = LocalDate(2026, 9, 14)
    MeetingTheme {
        CopyTargetDialog(
            sourceLabel = "09:00 – 10:00",
            month = buildCalendarMonth(today.yearMonth),
            scheduleCalendarInfo = ScheduleCalendarInfo(
                visibleMonth = today.yearMonth,
                selectedDate = today,
                today = today,
            ),
            onPreviousMonth = {},
            onNextMonth = {},
            onDateSelect = {},
            onConfirm = {},
            onDismiss = {}
        )
    }
}