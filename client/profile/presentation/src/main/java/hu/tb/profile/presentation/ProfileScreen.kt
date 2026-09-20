package hu.tb.profile.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.datastore.ProfileType
import hu.tb.design_system.Icons
import hu.tb.design_system.component.CardComponent
import hu.tb.design_system.component.CountdownSnackbar
import hu.tb.design_system.component.CountdownSnackbarVisuals
import hu.tb.design_system.component.DeleteProfileDialog
import hu.tb.design_system.component.LoadingDialog
import hu.tb.profile.presentation.component.SupportThanksDialog
import hu.tb.design_system.modifier.glowBackground
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.profile.domain.SupportInfo
import hu.tb.profile.presentation.component.SupportOption
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onBack: () -> Unit,
    onClearedProfile: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isThankYouDialogVisible by remember { mutableStateOf(false) }
    var isDeleteLoadingDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is ProfileEvent.Failed -> {
                    isDeleteLoadingDialogVisible = false
                    snackbarHostState.showSnackbar(
                        visuals = CountdownSnackbarVisuals(message = it.errorMessage)
                    )
                }

                ProfileEvent.ProfileCleared -> onClearedProfile()
                ProfileEvent.ShowDeleteLoadingDialog -> isDeleteLoadingDialogVisible = true
                ProfileEvent.ShowThankYouDialog -> isThankYouDialogVisible = true
            }
        }
    }

    val activity = LocalActivity.current

    ProfileScreen(
        snackbarHostState = snackbarHostState,
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = {
            when (it) {
                ProfileAction.OnBackClick -> onBack()
                ProfileAction.OnDeleteConfirmed -> viewModel.deleteProfile()
                ProfileAction.OnLogoutClick -> viewModel.logout()
                is ProfileAction.SupportOptionClick -> activity?.let { activity ->
                    viewModel.supportOption(
                        activity,
                        it.supportInfo
                    )
                }
            }
        }
    )

    if (isDeleteLoadingDialogVisible) {
        LoadingDialog(text = "Deleting profile…")
    }
    if (isThankYouDialogVisible) {
        SupportThanksDialog(
            onDismiss = { isThankYouDialogVisible = false }
        )
    }
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
            .glowBackground()
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
                state.profileType?.let {
                    Details(name = state.name, profileType = it)
                }
                SupportSection(
                    options = state.supportOptions,
                    onOptionClick = {
                        action(ProfileAction.SupportOptionClick(it))
                    }
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { action(ProfileAction.OnLogoutClick) },
                ) {
                    Text(
                        text = "Log out",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { isDeleteDialogVisible = true },
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
    }
}

@Composable
private fun SupportSection(
    options: List<SupportInfo>,
    onOptionClick: (SupportInfo) -> Unit
) {
    if (options.isEmpty()) return

    CardComponent {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                text = "Support the developer",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val isAnyPurchasing = options.any { it.isPurchasing }

            options.forEachIndexed { index, option ->
                SupportOption(
                    displayName = option.displayName,
                    description = option.description,
                    price = option.price,
                    isPurchasing = option.isPurchasing,
                    enabled = !isAnyPurchasing || option.isPurchasing,
                    onClick = { onOptionClick(option) }
                )
                if (index != options.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                }
            }
        }
    }
}

@Composable
private fun Details(
    name: String,
    profileType: ProfileType,
) {
    CardComponent {
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
            DetailRow(label = "Name", value = name)
            HorizontalDivider()
            DetailRow(
                label = "Account type",
                value = when (profileType) {
                    ProfileType.COACH -> "Coach"
                    ProfileType.NORMAL -> "Normal"
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

@PreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    MeetingTheme {
        ProfileScreen(
            snackbarHostState = SnackbarHostState(),
            state = ProfileState(
                name = "Theo",
                profileType = ProfileType.COACH,
                supportOptions = listOf(
                    SupportInfo(
                        id = "small_tip",
                        displayName = "Small tip",
                        description = "Buy me a coffee",
                        price = "$1.99",
                        isPurchasing = false
                    ),
                    SupportInfo(
                        id = "medium_tip",
                        displayName = "Medium tip",
                        description = "Keep the lights on for a week",
                        price = "$4.99",
                        isPurchasing = false
                    )
                )
            ),
            action = {}
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPurchasingPreview() {
    MeetingTheme {
        ProfileScreen(
            snackbarHostState = SnackbarHostState(),
            state = ProfileState(
                name = "Theo",
                profileType = ProfileType.COACH,
                supportOptions = listOf(
                    SupportInfo(
                        id = "small_tip",
                        displayName = "Small tip",
                        description = "Buy me a coffee",
                        price = "$1.99",
                        isPurchasing = true
                    ),
                    SupportInfo(
                        id = "medium_tip",
                        displayName = "Medium tip",
                        description = "Keep the lights on for a week",
                        price = "$4.99",
                    )
                )
            ),
            action = {}
        )
    }
}
