package hu.tb.profile.presentation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.component.CountdownSnackbar
import hu.tb.design_system.component.CountdownSnackbarVisuals
import hu.tb.design_system.component.DeleteProfileDialog
import hu.tb.design_system.component.LoadingDialog
import hu.tb.design_system.modifier.authGlowBackground
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.domain.ProfileType
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onBack: () -> Unit,
    onDeleted: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is ProfileEvent.Failed ->
                    snackbarHostState.showSnackbar(
                        visuals = CountdownSnackbarVisuals(message = it.errorMessage)
                    )

                ProfileEvent.Deleted -> onDeleted()
            }
        }
    }

    ProfileScreen(
        snackbarHostState = snackbarHostState,
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = {
            when (it) {
                ProfileAction.OnBackClick -> onBack()
                ProfileAction.OnDeleteConfirmed -> viewModel.deleteProfile()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
private fun ProfileScreen(
    snackbarHostState: SnackbarHostState,
    state: ProfileState,
    action: (ProfileAction) -> Unit
) {
    var isDeleteDialogVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .authGlowBackground()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { action(ProfileAction.OnBackClick) }) {
                            Icon(
                                painter = painterResource(Icons.arrow_back),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    CountdownSnackbar(snackbarData = data)
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .screenPadding()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Details(state = state)
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    onClick = { isDeleteDialogVisible = true },
                    enabled = !state.isDeleting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(
                        text = "Delete profile",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        if (isDeleteDialogVisible) {
            DeleteProfileDialog(
                onConfirm = {
                    isDeleteDialogVisible = false
                    action(ProfileAction.OnDeleteConfirmed)
                },
                onDismiss = { isDeleteDialogVisible = false }
            )
        }
        if (state.isDeleting) {
            LoadingDialog(text = "Deleting profile…")
        }
    }
}

@Composable
private fun Details(
    state: ProfileState
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(64.dp),
                painter = painterResource(Icons.person),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            DetailRow(label = "Name", value = state.name)
            HorizontalDivider()
            DetailRow(
                label = "Account type",
                value = when (state.profileType) {
                    ProfileType.COACH -> "Coach"
                    ProfileType.NORMAL -> "Normal"
                    null -> "—"
                }
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    MeetingTheme {
        ProfileScreen(
            snackbarHostState = SnackbarHostState(),
            state = ProfileState(name = "Theo", profileType = ProfileType.COACH),
            action = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ProfileScreenDarkPreview() {
    MeetingTheme {
        ProfileScreen(
            snackbarHostState = SnackbarHostState(),
            state = ProfileState(name = "Theo", profileType = ProfileType.NORMAL),
            action = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenDeletingPreview() {
    MeetingTheme {
        ProfileScreen(
            snackbarHostState = SnackbarHostState(),
            state = ProfileState(
                name = "Theo",
                profileType = ProfileType.NORMAL,
                isDeleting = true
            ),
            action = {}
        )
    }
}
