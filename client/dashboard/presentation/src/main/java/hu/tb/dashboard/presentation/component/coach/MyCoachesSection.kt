package hu.tb.dashboard.presentation.component.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.component.common.AvailableRing
import hu.tb.dashboard.presentation.component.common.DashboardCard
import hu.tb.dashboard.presentation.component.common.SectionHeader
import hu.tb.dashboard.presentation.model.CoachItem
import hu.tb.design_system.theme.MeetingTheme

@Composable
internal fun MyCoachesSection(
    coaches: List<CoachItem>,
    modifier: Modifier = Modifier,
    onCoachClick: (String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionHeader(
            modifier = Modifier.padding(horizontal = 4.dp),
            title = "My coaches"
        )
        if (coaches.isEmpty()) {
            EmptyCoaches()
        } else {
            coaches.forEach { coach ->
                CoachRow(
                    coach = coach,
                    onClick = { onCoachClick(coach.id) }
                )
            }
        }
    }
}

@Composable
private fun CoachRow(
    coach: CoachItem,
    onClick: () -> Unit
) {
    DashboardCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(initials = coach.initials)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = coach.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                OpenHours(count = coach.openHourCount)
            }
            Button(
                onClick = onClick,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Book",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun Avatar(initials: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun OpenHours(count: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (count > 0) {
            AvailableRing()
        }
        Text(
            text = when (count) {
                0 -> "No open hours right now"
                1 -> "1 open hour"
                else -> "$count open hours"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyCoaches() {
    DashboardCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(20.dp),
            text = "You haven't signed up with any coach yet. Once you do, they'll show up here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@PreviewLightDark
@Composable
private fun MyCoachesSectionPreview(
    @PreviewParameter(CoachPreviewParameterProvider::class) mock: List<CoachItem>
) {
    MeetingTheme {
        MyCoachesSection(
            coaches = mock,
            onCoachClick = {}
        )
    }
}
