package hu.tb.schedule.presentation.component

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initial: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timeState = rememberTimePickerState(
        initialHour = initial.hour,
        initialMinute = initial.minute,
    )

    BasicAlertDialog(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = onDismiss,
        content = {
            ScheduleDialog(
                content = {
                    TimePicker(state = timeState)
                },
                onConfirm = { onConfirm(LocalTime(timeState.hour, timeState.minute)) },
                onDismiss = onDismiss
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun TimePickerDialogPreview() {
    MeetingTheme {
        TimePickerDialog(
            initial = LocalTime(9, 0),
            onConfirm = {},
            onDismiss = {}
        )
    }
}