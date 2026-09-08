package hu.tb.notification.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.component.Avatar
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.notification.domain.RequestNotification

@TraceRecomposition
@Composable
internal fun RequestItem(
    request: RequestNotification,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(initials = request.senderName.take(1).uppercase())
        Text(
            modifier = Modifier.weight(1f),
            text = "${request.senderName} sent a request",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(onClick = onAccept) {
            Icon(
                painter = painterResource(Icons.person_check),
                contentDescription = "Accept request",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onReject) {
            Icon(
                painter = painterResource(Icons.person_disable),
                contentDescription = "Reject request",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun RequestItemPreview() {
    MeetingTheme {
        RequestItem(
            request = RequestNotification(id = "1", senderName = "Example name"),
            onAccept = {},
            onReject = {}
        )
    }
}
