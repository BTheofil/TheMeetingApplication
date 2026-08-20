package hu.tb.design_system.modifier

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush

@Composable
fun Modifier.authGlowBackground(): Modifier =
    this
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
