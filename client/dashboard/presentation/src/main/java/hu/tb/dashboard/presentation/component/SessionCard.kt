package hu.tb.dashboard.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.ThemePreviews
import hu.tb.dashboard.presentation.component.common.DashboardCard
import hu.tb.dashboard.presentation.component.common.DashboardCardCornerSize
import hu.tb.dashboard.presentation.model.SessionItem
import hu.tb.dashboard.presentation.util.currentDate
import hu.tb.dashboard.presentation.util.formatDayLabel
import hu.tb.dashboard.presentation.util.formatTime
import hu.tb.design_system.Icons
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

@Composable
internal fun SessionCard(
    session: SessionItem,
    modifier: Modifier = Modifier,
    showDate: Boolean = false,
    onClick: () -> Unit
) {
    DashboardCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .then(
                    if (session.isNext) {
                        Modifier.border(
                            width = 4.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = .7f),
                            shape = RoundedCornerShape(DashboardCardCornerSize.dp)
                        )
                    } else Modifier
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeColumn(session = session)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildString {
                        append(session.counterpartName)
                        append(" · ")
                        append("${session.durationMinutes} min")
                        if (showDate) {
                            append(" · ")
                            append(session.date.formatDayLabel())
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TimeColumn(session: SessionItem) {
    Column(
        modifier = Modifier
            .width(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(Icons.schedule),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = session.start.formatTime(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = session.end.formatTime(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@ThemePreviews
@Composable
private fun SessionCardPreview() {
    MeetingTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SessionCard(
                session = SessionItem(
                    id = "s1",
                    title = "Leg day",
                    counterpartName = "Anna Kovács",
                    date = currentDate(),
                    start = LocalTime(9, 0),
                    durationMinutes = 60,
                    isNext = true
                ), onClick = {})
            SessionCard(
                session = SessionItem(
                    id = "s4",
                    title = "Upper body",
                    counterpartName = "Márk Szabó",
                    date = currentDate().plus(4, DateTimeUnit.DAY),
                    start = LocalTime(8, 0),
                    durationMinutes = 60
                ), showDate = true, onClick = {})
        }
    }
}
