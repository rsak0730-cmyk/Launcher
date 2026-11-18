package com.manish.liquidlauncher.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.liquidlauncher.model.AppInfo
import com.manish.liquidlauncher.model.LiquidThemeConfig

fun LiquidThemeConfig.accentColor(): Color {
    return Color.hsv(accentHue.coerceIn(0f, 360f), 0.78f, 1f)
}

@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    config: LiquidThemeConfig,
    cornerRadius: Dp = config.cornerRadiusDp.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val accent = config.accentColor()

    val bodyGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = config.refractionOpacity + 0.08f),
            accent.copy(alpha = config.refractionOpacity * 0.65f),
            Color(0xFF091220).copy(alpha = (config.refractionOpacity + 0.35f).coerceAtMost(0.85f))
        ),
        start = Offset(0f, 0f),
        end = Offset(500f, 500f)
    )

    val rimGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = (0.55f * config.glowIntensity).coerceIn(0.15f, 0.9f)),
            accent.copy(alpha = (0.45f * config.glowIntensity).coerceIn(0.1f, 0.8f)),
            Color.White.copy(alpha = 0.08f),
            accent.copy(alpha = (0.35f * config.glowIntensity).coerceIn(0.1f, 0.7f))
        ),
        start = Offset(0f, 0f),
        end = Offset(300f, 300f)
    )

    Box(
        modifier = modifier
            .shadow(elevation = 10.dp, shape = shape, ambientColor = accent, spotColor = accent)
            .clip(shape)
            .background(bodyGradient)
            .border(width = 1.25.dp, brush = rimGradient, shape = shape),
        content = content
    )
}

@Composable
fun LiquidAppIconItem(
    app: AppInfo,
    config: LiquidThemeConfig,
    showLabel: Boolean = true,
    iconBoxSize: Dp = 60.dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 08. Droplet Icon Elastic Scale Physics
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.86f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dropletScale"
    )

    val bitmap = remember(app.packageName) { drawableToBitmap(app.icon).asImageBitmap() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(4.dp)
    ) {
        LiquidGlassSurface(
            config = config,
            cornerRadius = (iconBoxSize.value * 0.34f).dp,
            modifier = Modifier.size(iconBoxSize)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Specular top-left droplet highlight
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .size(14.dp, 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.42f))
                )
                Image(
                    bitmap = bitmap,
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(iconBoxSize * 0.64f)
                        .clip(RoundedCornerShape(14.dp))
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = app.label,
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 128
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 128
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
