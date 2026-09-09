package hu.tb.notification.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.notification.domain.RequestDecision
import hu.tb.notification.domain.RequestNotification
import hu.tb.notification.presentation.component.RequestItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = koinViewModel(),
    navigationRequest: () -> Unit
) {
    NotificationScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = {
            when (it) {
                NotificationAction.BackRequest -> navigationRequest()
                is NotificationAction.RequestSelected -> viewModel.requestMade(it.decision, it.notificationId)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
private fun NotificationScreen(
    state: NotificationState,
    action: (NotificationAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { action(NotificationAction.BackRequest) }) {
                        Icon(
                            painter = painterResource(Icons.arrow_back),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .screenPadding()
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp)
                )

                state.requests.isEmpty() -> Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "No notifications to show yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items = state.requests, key = { it.id }) { request ->
                        RequestItem(
                            request = request,
                            onAccept = {
                                action(
                                    NotificationAction.RequestSelected(
                                        RequestDecision.ACCEPT,
                                        request.id
                                    )
                                )
                            },
                            onReject = {
                                action(
                                    NotificationAction.RequestSelected(
                                        RequestDecision.REJECT,
                                        request.id
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun NotificationScreenPreview() {
    MeetingTheme {
        NotificationScreen(
            state = NotificationState(
                requests = listOf(
                    RequestNotification(id = "1", senderName = "Example name"),
                    RequestNotification(id = "2", senderName = "Other name")
                )
            ),
            action = {}
        )
    }
}