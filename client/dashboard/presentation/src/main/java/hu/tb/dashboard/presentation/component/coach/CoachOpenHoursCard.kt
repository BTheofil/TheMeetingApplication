package hu.tb.dashboard.presentation.component.coach

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
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.ThemePreviews
import hu.tb.dashboard.presentation.component.common.DashboardCard
import hu.tb.design_system.Icons
import hu.tb.design_system.theme.MeetingTheme

@Composable
internal fun CoachOpenHoursCard(
    modifier: Modifier = Modifier,
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

@ThemePreviews
@Composable
private fun CoachOpenHoursCardPreview() {
    MeetingTheme {
        CoachOpenHoursCard(onCreateOpenHours = {})
    }
}
