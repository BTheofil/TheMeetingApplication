package hu.tb.dashboard.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.domain.FreeSession
import hu.tb.dashboard.presentation.util.formatTime
import hu.tb.design_system.Icons
import hu.tb.design_system.component.CardComponent
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Composable
internal fun OpenSlotCard(
    slot: FreeSession,
    coachName: String?,
    onClick: () -> Unit
) {
    CardComponent {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeColumn(slot = slot)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = coachName ?: "Open hour",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            OutlinedButton(
                onClick = onClick
            ) {
                Text(
                    text = "Book",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun TimeColumn(slot: FreeSession) {
    Column(
        modifier = Modifier
            .width(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(Icons.schedule),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Text(
            text = slot.start.formatTime(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Text(
            text = slot.end.formatTime(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
        )
    }
}

@PreviewLightDark
@Composable
private fun OpenSlotCardPreview() {
    MeetingTheme {
        OpenSlotCard(
            slot = FreeSession(
                "coach-anna",
                LocalDate(2026, 6, 6),
                LocalTime(15, 0),
                LocalTime(16, 0)
            ),
            coachName = "Anna Kovács",
            onClick = {}
        )
    }
}
