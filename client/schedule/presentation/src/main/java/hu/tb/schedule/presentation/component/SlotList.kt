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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.schedule.domain.SlotListInfo
import hu.tb.schedule.domain.TimeRange
import hu.tb.schedule.domain.formattedTimeUi
import hu.tb.schedule.domain.overlaps
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
    isPastDay: Boolean,
    deletingSessionIds: Set<Int>,
    onCopyDay: () -> Unit,
    onPasteDay: () -> Unit,
    onDeleteSession: (TimeRange.SessionTime) -> Unit,
    onDeleteDraft: (TimeRange.DraftSlot) -> Unit,
    onCopy: (TimeRange) -> Unit,
    onConfirmDraft: (TimeRange.DraftSlot) -> Unit
) {
    var draft by remember(slotListInfo.date) { mutableStateOf<TimeRange.DraftSlot?>(null) }
    var timeTarget by remember(slotListInfo.date) { mutableStateOf<TimeTarget?>(null) }

    val isDayEmpty = slotListInfo.sessions.isEmpty() && slotListInfo.drafts.isEmpty()

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
                enabled = isPasteEnabled && !isPastDay
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

        slotListInfo.sessions.forEach { session ->
            SlotRow(
                time = session.formattedTimeUi(),
                isDraft = false,
                isDeleting = session.id in deletingSessionIds,
                onCopy = { onCopy(session) },
                onDelete = { onDeleteSession(session) }
            )
        }

        slotListInfo.drafts.forEach { draftSlot ->
            SlotRow(
                time = draftSlot.formattedTimeUi(),
                isDraft = true,
                onCopy = { onCopy(draftSlot) },
                onDelete = { onDeleteDraft(draftSlot) }
            )
        }

        if (currentDraft != null) {
            SlotDraftRow(
                draft = currentDraft,
                isConfirmEnabled = currentDraft.end > currentDraft.start &&
                        slotListInfo.sessions.none { it.overlaps(currentDraft) } &&
                        slotListInfo.drafts.none { it.overlaps(currentDraft) },
                onStartClick = { timeTarget = TimeTarget.START },
                onEndClick = { timeTarget = TimeTarget.END },
                onConfirm = {
                    onConfirmDraft(currentDraft)
                    draft = null
                },
                onCancel = { draft = null }
            )
        } else if (!isPastDay) {
            TextButton(
                onClick = {
                    val start = (slotListInfo.sessions.map { it.end } +
                            slotListInfo.drafts.map { it.end }).maxOrNull()
                    draft = TimeRange.DraftSlot(
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
    isDeleting: Boolean = false,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isDeleting) 0.5f else 1f),
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
        IconButton(
            onClick = onDelete,
            enabled = !isDeleting
        ) {
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
    draft: TimeRange.DraftSlot,
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
                sessions = listOf(
                    TimeRange.SessionTime(1, LocalTime(9, 0), LocalTime(10, 0)),
                    TimeRange.SessionTime(2, LocalTime(11, 0), LocalTime(11, 30))
                ),
                drafts = listOf(
                    TimeRange.DraftSlot(LocalTime(13, 0), LocalTime(14, 0))
                )
            ),
            isPasteEnabled = true,
            isPastDay = false,
            deletingSessionIds = emptySet(),
            onCopyDay = {},
            onPasteDay = {},
            onDeleteSession = {},
            onDeleteDraft = {},
            onCopy = {},
            onConfirmDraft = {}
        )
    }
}
