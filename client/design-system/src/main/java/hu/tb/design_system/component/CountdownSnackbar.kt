package hu.tb.design_system.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.tb.design_system.theme.MeetingTheme

class CountdownSnackbarVisuals(
    override val message: String,
) : SnackbarVisuals {
    override val actionLabel: String? = null
    override val withDismissAction: Boolean = true
    override val duration: SnackbarDuration = SnackbarDuration.Short
}

@Composable
fun CountdownSnackbar(
    snackbarData: SnackbarData
) {
    val duration = (snackbarData.visuals as? CountdownSnackbarVisuals)?.duration
    val progress = remember { Animatable(1f) }

    LaunchedEffect(snackbarData) {
        if (duration != null) {
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = when (duration) {
                        SnackbarDuration.Short -> 4_000
                        SnackbarDuration.Long -> 10_000
                        SnackbarDuration.Indefinite -> Int.MAX_VALUE
                    }, easing = LinearEasing
                )
            )
        }
    }

    Snackbar(
        shape = RoundedCornerShape(12.dp),
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    progress = { progress.value },
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    trackColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.25f)
                )
                Text(
                    text = snackbarData.visuals.message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

private class PreviewSnackbarData(
    override val visuals: SnackbarVisuals
) : SnackbarData {
    override fun performAction() = Unit
    override fun dismiss() = Unit
}

@Preview
@Composable
private fun CountdownSnackbarPreview() {
    MeetingTheme {
        CountdownSnackbar(
            snackbarData = PreviewSnackbarData(
                visuals = CountdownSnackbarVisuals(
                    message = "Server error"
                )
            )
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CountdownSnackbarDarkPreview() {
    MeetingTheme {
        CountdownSnackbar(
            snackbarData = PreviewSnackbarData(
                visuals = CountdownSnackbarVisuals(
                    message = "Server error"
                )
            )
        )
    }
}
