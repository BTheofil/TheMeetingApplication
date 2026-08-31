package hu.tb.dashboard.presentation.component.coach

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.component.common.DashboardCard
import hu.tb.design_system.Icons
import hu.tb.design_system.theme.MeetingTheme

@Composable
internal fun CoachOpenHoursCard(
    modifier: Modifier = Modifier,
    openSlotCount: Int,
    onCreateOpenHours: () -> Unit
) {
    DashboardCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(Icons.schedule),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Let clients book you",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = if (openSlotCount == 0) {
                    "Clients can only book a session once you share when you are free."
                } else {
                    "$openSlotCount open ${if (openSlotCount == 1) "slot" else "slots"} published this month. Add more so clients can find a time that fits."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = onCreateOpenHours
            ) {
                Text(
                    text = "Set open hours",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoachOpenHoursCardPreview() {
    MeetingTheme {
        CoachOpenHoursCard(openSlotCount = 5, onCreateOpenHours = {})
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CoachOpenHoursCardEmptyPreview() {
    MeetingTheme {
        CoachOpenHoursCard(openSlotCount = 0, onCreateOpenHours = {})
    }
}
