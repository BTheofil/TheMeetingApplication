package hu.tb.presentation.form

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.component.CountdownSnackbar
import hu.tb.design_system.component.CountdownSnackbarVisuals
import hu.tb.design_system.component.LoadingDialog
import hu.tb.design_system.modifier.authGlowBackground
import hu.tb.design_system.modifier.clearFocus
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.domain.AuthMode
import hu.tb.domain.LoginForm
import hu.tb.domain.ProfileType
import hu.tb.domain.RegisterForm
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthFormScreen(
    viewModel: AuthFormViewModel = koinViewModel(),
    navigationRequest: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is AuthFormEvent.Failed ->
                    snackbarHostState.showSnackbar(
                        visuals = CountdownSnackbarVisuals(
                            message = it.cause
                        )
                    )

                AuthFormEvent.Success -> navigationRequest()
            }
        }
    }

    AuthFormScreen(
        snackbarHostState = snackbarHostState,
        state = viewModel.state.collectAsStateWithLifecycle().value,
        action = {
            when (it) {
                is AuthFormAction.OnLogin -> viewModel.login(it.form)
                is AuthFormAction.OnRegister -> viewModel.register(it.form)
            }
        }
    )
}

@TraceRecomposition
@Composable
private fun AuthFormScreen(
    snackbarHostState: SnackbarHostState,
    state: AuthFormState,
    action: (AuthFormAction) -> Unit
) {
    val nameTFS = remember { TextFieldState() }
    val passwordTFS = remember { TextFieldState() }
    val rePasswordTFS = remember { TextFieldState() }
    var profileType by remember { mutableStateOf<ProfileType?>(null) }

    val isFormValid by remember(state.mode) {
        derivedStateOf {
            when (state.mode) {
                AuthMode.LOGIN ->
                    nameTFS.text.isNotBlank() && passwordTFS.text.isNotBlank()

                AuthMode.REGISTER ->
                    nameTFS.text.isNotBlank() &&
                            passwordTFS.text == rePasswordTFS.text &&
                            profileType != null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .authGlowBackground()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    CountdownSnackbar(snackbarData = data)
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .clearFocus(),
                contentAlignment = Alignment.Center
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .screenPadding()
                        .padding(horizontal = 8.dp),
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
                        CardHeader(mode = state.mode)
                        Form(
                            mode = state.mode,
                            nameTFS = nameTFS,
                            passwordTFS = passwordTFS,
                            rePasswordTFS = rePasswordTFS,
                            profileType = profileType,
                            onTypeClick = { profileType = it })
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            onClick = {
                                when (state.mode) {
                                    AuthMode.LOGIN -> action(
                                        AuthFormAction.OnLogin(
                                            LoginForm(
                                                username = nameTFS.text.toString(),
                                                password = passwordTFS.text.toString()
                                            )
                                        )
                                    )

                                    AuthMode.REGISTER -> action(
                                        AuthFormAction.OnRegister(
                                            RegisterForm(
                                                username = nameTFS.text.toString(),
                                                password = passwordTFS.text.toString(),
                                                type = profileType ?: ProfileType.NORMAL
                                            )
                                        )
                                    )
                                }
                            },
                            enabled = isFormValid,
                            content = {
                                Text(
                                    text = when (state.mode) {
                                        AuthMode.LOGIN -> "Log in"
                                        AuthMode.REGISTER -> "Create account"
                                    },
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                        )
                    }
                }
            }
            if (state.isLoading) {
                LoadingDialog(
                    text = when (state.mode) {
                        AuthMode.LOGIN -> "Signing in…"
                        AuthMode.REGISTER -> "Creating account…"
                    }
                )
            }
        }
    }
}

@Composable
private fun CardHeader(
    mode: AuthMode
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (mode) {
                AuthMode.LOGIN -> "Welcome back"
                AuthMode.REGISTER -> "Create account"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = when (mode) {
                AuthMode.LOGIN -> "Sign in to continue"
                AuthMode.REGISTER -> "Sign up to get started"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun Form(
    mode: AuthMode,
    nameTFS: TextFieldState,
    passwordTFS: TextFieldState,
    rePasswordTFS: TextFieldState,
    profileType: ProfileType?,
    onTypeClick: (ProfileType) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isRePasswordVisible by remember { mutableStateOf(false) }

    val passwordsMismatch by remember {
        derivedStateOf {
            rePasswordTFS.text.isNotEmpty() && passwordTFS.text != rePasswordTFS.text
        }
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        state = nameTFS,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        label = {
            Text(
                text = "Your name",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
    OutlinedSecureTextField(
        modifier = Modifier.fillMaxWidth(),
        state = passwordTFS,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrectEnabled = false,
            imeAction = when (mode) {
                AuthMode.LOGIN -> ImeAction.Done
                AuthMode.REGISTER -> ImeAction.Next
            }
        ),
        textObfuscationMode = if (isPasswordVisible) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.RevealLastTyped
        },
        label = {
            Text(
                text = "Password",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            PasswordVisibilityIcon(
                isVisible = isPasswordVisible,
                onToggle = { isPasswordVisible = !isPasswordVisible }
            )
        },
    )
    if (mode == AuthMode.REGISTER) {
        OutlinedSecureTextField(
            modifier = Modifier.fillMaxWidth(),
            state = rePasswordTFS,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done
            ),
            textObfuscationMode = if (isRePasswordVisible) {
                TextObfuscationMode.Visible
            } else {
                TextObfuscationMode.RevealLastTyped
            },
            isError = passwordsMismatch,
            label = {
                Text(
                    text = "Repeat password",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                PasswordVisibilityIcon(
                    isVisible = isRePasswordVisible,
                    onToggle = { isRePasswordVisible = !isRePasswordVisible }
                )
            },
            supportingText = {
                AnimatedVisibility(visible = passwordsMismatch) {
                    Text(
                        text = "Passwords do not match",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "I am a",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = profileType == ProfileType.COACH,
                    onClick = { onTypeClick(ProfileType.COACH) },
                    label = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Coach",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                )
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = profileType == ProfileType.NORMAL,
                    onClick = { onTypeClick(ProfileType.NORMAL) },
                    label = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Normal",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun PasswordVisibilityIcon(
    isVisible: Boolean,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Icon(
            painter = painterResource(
                if (isVisible) Icons.visibility else Icons.visibility_off
            ),
            contentDescription = if (isVisible) "Hide password" else "Show password",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenRegisterPreview() {
    MeetingTheme {
        AuthFormScreen(
            snackbarHostState = SnackbarHostState(),
            state = AuthFormState(),
            action = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AuthScreenRegisterDarkPreview() {
    MeetingTheme {
        AuthFormScreen(
            snackbarHostState = SnackbarHostState(),
            state = AuthFormState(),
            action = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenLoginPreview() {
    MeetingTheme {
        AuthFormScreen(
            snackbarHostState = SnackbarHostState(),
            state = AuthFormState(mode = AuthMode.LOGIN),
            action = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AuthScreenLoginDarkPreview() {
    MeetingTheme {
        AuthFormScreen(
            snackbarHostState = SnackbarHostState(),
            state = AuthFormState(mode = AuthMode.LOGIN),
            action = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenLoadingPreview() {
    MeetingTheme {
        AuthFormScreen(
            snackbarHostState = SnackbarHostState(),
            state = AuthFormState(mode = AuthMode.LOGIN, isLoading = true),
            action = {}
        )
    }
}
