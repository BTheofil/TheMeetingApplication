package hu.tb.dashboard.presentation.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

internal val IndicatorSize = 7.dp

@Composable
internal fun BookedDot(
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    Box(
        modifier = modifier
            .size(IndicatorSize)
            .padding(1.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
    )
}

@Composable
internal fun AvailableRing(
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    Box(
        modifier = modifier
            .size(IndicatorSize)
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}
