package hu.tb.design_system.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
fun LoadingDialog(
    text: String
) {
    BasicAlertDialog(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        content = { LoadingContent(text = text) }
    )
}

@Composable
private fun LoadingContent(
    text: String
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
            CircularProgressIndicator()
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
private fun LoadingDialogPreview() {
    MeetingTheme {
        LoadingContent(text = "Signing in…")
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LoadingDialogDarkPreview() {
    MeetingTheme {
        LoadingContent(text = "Creating account…")
    }
}
