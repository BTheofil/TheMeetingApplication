package hu.tb.schedule.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.schedule.domain.DraftSlot
import hu.tb.schedule.domain.SlotListInfo
import hu.tb.schedule.domain.TimeSlot
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

@TraceRecomposition
@Composable
internal fun SlotList(
    slotListInfo: SlotListInfo,
    modifier: Modifier = Modifier,
    onCopyDay: () -> Unit,
    onCopySlot: (TimeSlot) -> Unit,
    onDeleteSlot: (TimeSlot) -> Unit,
    onDraftConfirm: (DraftSlot) -> Unit
) {
    var draft by remember(slotListInfo.date) { mutableStateOf<DraftSlot?>(null) }
    var timeTarget by remember(slotListInfo.date) { mutableStateOf<TimeTarget?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = slotListInfo.date.format(dayLabelFormat),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(
                onClick = onCopyDay,
                enabled = slotListInfo.slots.isNotEmpty()
            ) {
                Text(
                    text = "Copy day",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        val currentDraft = draft
        if (slotListInfo.slots.isEmpty() && currentDraft == null) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "No open hours yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        slotListInfo.slots.forEach { slot ->
            SlotRow(
                slot = slot,
                onCopy = { onCopySlot(slot) },
                onDelete = { onDeleteSlot(slot) }
            )
        }

        if (currentDraft != null) {
            SlotDraftRow(
                draft = currentDraft,
                isConfirmEnabled = currentDraft.end > currentDraft.start,
                onStartClick = { timeTarget = TimeTarget.START },
                onEndClick = { timeTarget = TimeTarget.END },
                onConfirm = {
                    onDraftConfirm(currentDraft)
                    draft = null
                },
                onCancel = { draft = null }
            )
        } else {
            TextButton(
                onClick = {
                    val start = slotListInfo.slots.lastOrNull()?.end
                    draft = DraftSlot(
                        start = start ?: LocalTime(9, 0),
                        end = start ?: LocalTime(10, 0)
                    )
                }
            ) {
                Icon(
                    painter = painterResource(Icons.add),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Add time slot",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    if (draft != null && timeTarget != null) {
        TimePickerDialog(
            initial = when (timeTarget!!) {
                TimeTarget.START -> draft!!.start
                TimeTarget.END -> draft!!.end
            },
            onConfirm = { picked ->
                draft = when (timeTarget!!) {
                    TimeTarget.START -> draft?.copy(start = picked)
                    TimeTarget.END -> draft?.copy(end = picked)
                }
                timeTarget = null
            },
            onDismiss = { timeTarget = null }
        )
    }
}

private enum class TimeTarget { START, END }

private val dayLabelFormat = LocalDate.Format {
    dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
    chars(", ")
    day(Padding.NONE)
    char(' ')
    monthName(MonthNames.ENGLISH_FULL)
}

@Composable
private fun SlotRow(
    slot: TimeSlot,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = slot.formattedTimeUi(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onCopy) {
            Icon(
                painter = painterResource(Icons.copy),
                contentDescription = "icon copy",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(Icons.delete),
                contentDescription = "delete copy",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SlotDraftRow(
    draft: DraftSlot,
    isConfirmEnabled: Boolean,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AssistChip(
            onClick = onStartClick,
            label = {
                Text(text = draft.start.format(LocalTime.Format {
                    hour()
                    char(':')
                    minute()
                }))
            }
        )
        Text(
            text = "–",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AssistChip(
            onClick = onEndClick,
            label = {
                Text(text = draft.end.format(LocalTime.Format {
                    hour()
                    char(':')
                    minute()
                }))
            }
        )
        IconButton(
            onClick = onConfirm,
            enabled = isConfirmEnabled
        ) {
            Icon(
                painter = painterResource(Icons.check),
                contentDescription = "Save time slot",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onCancel) {
            Icon(
                painter = painterResource(Icons.close),
                contentDescription = "Cancel time slot",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SlotListPreview() {
    MeetingTheme {
        SlotList(
            slotListInfo = SlotListInfo(
                date = LocalDate(2026, 9, 14),
                slots = listOf(
                    TimeSlot(1, LocalTime(9, 0), LocalTime(10, 0)),
                    TimeSlot(2, LocalTime(11, 0), LocalTime(11, 30))
                )
            ),
            onCopyDay = {},
            onCopySlot = {},
            onDeleteSlot = {},
            onDraftConfirm = {}
        )
    }
}
