package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.util.formatMonthLabel
import hu.tb.design_system.Icons
import hu.tb.design_system.theme.MeetingTheme
import kotlinx.datetime.YearMonth

@Composable
internal fun CalendarHeader(
    visibleMonth: YearMonth,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToggleExpanded: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(Icons.calendar),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.weight(1f),
            text = visibleMonth.formatMonthLabel(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (isExpanded) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    painter = painterResource(Icons.chevron_left),
                    contentDescription = "Previous month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onNextMonth) {
                Icon(
                    painter = painterResource(Icons.chevron_right),
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(onClick = onToggleExpanded) {
            Icon(
                painter = painterResource(Icons.expand),
                contentDescription = if (isExpanded) "Collapse calendar" else "Expand calendar",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarHeaderPreview() {
    MeetingTheme {
        CalendarHeader(
            visibleMonth = YearMonth(2026, 8),
            isExpanded = false,
            onPreviousMonth = {},
            onNextMonth = {},
            onToggleExpanded = {}
        )
    }
}
