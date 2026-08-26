package hu.tb.design_system.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import hu.tb.design_system.theme.MeetingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionExpiredDialog(
    onConfirm: () -> Unit
) {
    BasicAlertDialog(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        content = { SessionExpiredContent(onConfirm = onConfirm) }
    )
}

@Composable
private fun SessionExpiredContent(
    onConfirm: () -> Unit
) {
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Session expired",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Please sign in again to continue.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = onConfirm
            ) {
                Text(
                    text = "Log in",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview
@Composable
private fun SessionExpiredDialogPreview() {
    MeetingTheme {
        SessionExpiredContent(onConfirm = {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SessionExpiredDialogDarkPreview() {
    MeetingTheme {
        SessionExpiredContent(onConfirm = {})
    }
}
