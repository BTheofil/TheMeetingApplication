package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import hu.tb.dashboard.presentation.util.narrowLabel
import kotlinx.datetime.DayOfWeek

internal val WeekDays: List<DayOfWeek> = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
    DayOfWeek.SUNDAY
)

@Composable
internal fun WeekdayLabels(
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        WeekDays.forEach { dayOfWeek ->
            Text(
                modifier = Modifier.weight(1f),
                text = dayOfWeek.narrowLabel(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
