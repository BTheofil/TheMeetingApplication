package hu.tb.dashboard.presentation.component.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import hu.tb.dashboard.presentation.component.common.AvailableRing
import hu.tb.dashboard.presentation.component.common.BookedDot
import hu.tb.dashboard.presentation.component.common.IndicatorSize
import hu.tb.dashboard.presentation.model.CalendarDay
import hu.tb.design_system.theme.MeetingTheme

@Composable
internal fun DayCell(
    modifier: Modifier = Modifier,
    day: CalendarDay,
    isSelected: Boolean,
    isToday: Boolean,
    isInVisibleMonth: Boolean = true,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "dayCellContainer"
    )
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !isInVisibleMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(containerColor)
                .then(
                    if (isToday && !isSelected) {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
        Indicators(day = day, isInVisibleMonth = isInVisibleMonth)
    }
}

private const val MAX_DOTS = 3

@Composable
private fun Indicators(
    day: CalendarDay,
    isInVisibleMonth: Boolean
) {
    val dimAlpha = if (isInVisibleMonth) 1f else 0.38f

    Row(
        modifier = Modifier.height(IndicatorSize),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(day.sessionCount.coerceAtMost(MAX_DOTS)) {
            BookedDot(alpha = dimAlpha)
        }
        if (day.hasOpenSlot) {
            AvailableRing(alpha = dimAlpha)
        }
    }
}

@PreviewLightDark
@Composable
private fun DayCellPreview(
    @PreviewParameter(CalendarDayPreviewParameterProvider::class) day: CalendarDay
) {
    MeetingTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            DayCell(day = day, isSelected = false, isToday = false, onClick = {})
            DayCell(day = day, isSelected = false, isToday = true, onClick = {})
            DayCell(day = day, isSelected = true, isToday = false, onClick = {})
            DayCell(
                day = day,
                isSelected = false,
                isToday = false,
                isInVisibleMonth = false,
                onClick = {})
        }
    }
}
