package hu.tb.dashboard.presentation.component.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import hu.tb.design_system.theme.MeetingTheme

@Composable
internal fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingLabel: String? = null,
    onTrailingClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (trailingLabel != null && onTrailingClick != null) {
            TextButton(onClick = onTrailingClick) {
                Text(
                    text = trailingLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SectionHeaderPreview() {
    MeetingTheme {
        SectionHeader(
            title = "Available slots",
            trailingLabel = "See all",
            onTrailingClick = {}
        )
    }
}
