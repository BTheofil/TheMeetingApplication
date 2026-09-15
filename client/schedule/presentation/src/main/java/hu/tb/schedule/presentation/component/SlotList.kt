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
import hu.tb.schedule.domain.formattedTimeUi
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
    isPasteEnabled: Boolean,
    onCopyDay: () -> Unit,
    onPasteDay: () -> Unit,
    onCopySlot: (DraftSlot) -> Unit,
    onDeleteSlot: (TimeSlot) -> Unit,
    onDeleteDraft: (DraftSlot) -> Unit,
    onDraftConfirm: (DraftSlot) -> Unit
) {
    var draft by remember(slotListInfo.date) { mutableStateOf<DraftSlot?>(null) }
    var timeTarget by remember(slotListInfo.date) { mutableStateOf<TimeTarget?>(null) }

    val isDayEmpty = slotListInfo.slots.isEmpty() && slotListInfo.drafts.isEmpty()

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
                enabled = !isDayEmpty
            ) {
                Text(
                    text = "Copy day",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            TextButton(
                onClick = onPasteDay,
                enabled = isPasteEnabled
            ) {
                Text(
                    text = "Paste",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        val currentDraft = draft
        if (isDayEmpty && currentDraft == null) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "No open hours yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        slotListInfo.slots.forEach { slot ->
            SlotRow(
                time = slot.formattedTimeUi(),
                isDraft = false,
                onCopy = { onCopySlot(slot.toDraft()) },
                onDelete = { onDeleteSlot(slot) }
            )
        }

        slotListInfo.drafts.forEach { draftSlot ->
            SlotRow(
                time = draftSlot.formattedTimeUi(),
                isDraft = true,
                onCopy = { onCopySlot(draftSlot) },
                onDelete = { onDeleteDraft(draftSlot) }
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
                    val start = (slotListInfo.slots.map { it.end } +
                            slotListInfo.drafts.map { it.end }).maxOrNull()
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
    time: String,
    isDraft: Boolean,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = if (isDraft) "$time · unpublished" else time,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDraft) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface
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
            label = { Text(text = draft.start.formattedTimeUi()) }
        )
        Text(
            text = "–",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AssistChip(
            onClick = onEndClick,
            label = { Text(text = draft.end.formattedTimeUi()) }
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
                ),
                drafts = listOf(
                    DraftSlot(LocalTime(13, 0), LocalTime(14, 0))
                )
            ),
            isPasteEnabled = true,
            onCopyDay = {},
            onPasteDay = {},
            onCopySlot = {},
            onDeleteSlot = {},
            onDeleteDraft = {},
            onDraftConfirm = {}
        )
    }
}
