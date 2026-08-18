package hu.tb.presentation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.modifier.clearFocus
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.domain.ProfileType
import hu.tb.domain.RegisterForm
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel()
) {
    AuthScreen(
        onSubmit = { viewModel.submitForm(it) }
    )
}

@TraceRecomposition
@Composable
private fun AuthScreen(
    onSubmit: (RegisterForm) -> Unit
) {
    val nameTFS = remember { TextFieldState() }
    val passwordTFS = remember { TextFieldState() }
    val rePasswordTFS = remember { TextFieldState() }
    var profileType by remember { mutableStateOf<ProfileType?>(null) }

    val isFormValid by remember {
        derivedStateOf {
            nameTFS.text.isNotBlank() &&
                    passwordTFS.text == rePasswordTFS.text &&
                    profileType != null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .background(
                glowBrush(
                    color = MaterialTheme.colorScheme.primary,
                    centerFraction = Offset(0.5f, 0.18f),
                    radiusFraction = 0.85f,
                    glowAlpha = 0.28f
                )
            )
            .background(
                glowBrush(
                    color = MaterialTheme.colorScheme.secondary,
                    centerFraction = Offset(0.9f, 0.95f),
                    radiusFraction = 0.6f,
                    glowAlpha = 0.22f
                )
            )
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
                CardHeader()
                Form(
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
                        onSubmit(
                            RegisterForm(
                                username = nameTFS.text.toString(),
                                password = passwordTFS.text.toString(),
                                type = profileType ?: ProfileType.NORMAL
                            )
                        )
                    },
                    enabled = isFormValid,
                    content = {
                        Text(
                            text = "Create account",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun CardHeader() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create account",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Sign up to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun Form(
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
            imeAction = ImeAction.Next
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
            VisibilityToggle(
                isVisible = isPasswordVisible,
                onToggle = { isPasswordVisible = !isPasswordVisible }
            )
        },
    )
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
            VisibilityToggle(
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

@Composable
private fun VisibilityToggle(
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

@Composable
private fun glowBrush(
    color: Color,
    centerFraction: Offset,
    radiusFraction: Float,
    glowAlpha: Float
): Brush = remember(color, centerFraction, radiusFraction, glowAlpha) {
    object : ShaderBrush() {
        override fun createShader(size: Size): Shader =
            RadialGradientShader(
                center = Offset(
                    x = size.width * centerFraction.x,
                    y = size.height * centerFraction.y
                ),
                radius = size.maxDimension * radiusFraction,
                colors = listOf(color.copy(alpha = glowAlpha), Color.Transparent)
            )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    MeetingTheme {
        AuthScreen(
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AuthScreenDarkPreview() {
    MeetingTheme {
        AuthScreen(
            onSubmit = {}
        )
    }
}
