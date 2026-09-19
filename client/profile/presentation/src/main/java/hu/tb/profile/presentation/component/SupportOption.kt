package hu.tb.profile.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hu.tb.design_system.component.CardComponent
import hu.tb.design_system.theme.MeetingTheme

@Composable
fun SupportOption(
    displayName: String,
    price: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isPurchasing: Boolean = false,
    enabled: Boolean = true,
) {
    val contentAlpha = if (enabled) 1f else 0.38f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = enabled && !isPurchasing,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                )
            }
        }
        PriceBadge(
            price = price,
            isPurchasing = isPurchasing,
            contentAlpha = contentAlpha
        )
    }
}

@Composable
private fun PriceBadge(
    price: String,
    isPurchasing: Boolean,
    contentAlpha: Float
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 76.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = contentAlpha))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isPurchasing) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            Text(
                text = price,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = contentAlpha)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SupportOptionPreview() {
    MeetingTheme {
        CardComponent {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                SupportOption(
                    displayName = "Small tip",
                    description = "Buy me a coffee",
                    price = "$1.99",
                    onClick = {}
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                SupportOption(
                    displayName = "Medium tip",
                    description = "Keep the lights on for a week",
                    price = "$4.99",
                    onClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SupportOptionPurchasingPreview() {
    MeetingTheme {
        CardComponent {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                SupportOption(
                    displayName = "Small tip",
                    description = "Buy me a coffee",
                    price = "$1.99",
                    isPurchasing = true,
                    onClick = {}
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                SupportOption(
                    displayName = "Medium tip",
                    description = "Keep the lights on for a week",
                    price = "$4.99",
                    enabled = false,
                    onClick = {}
                )
            }
        }
    }
}
