package com.manish.liquidlauncher.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.liquidlauncher.model.AppInfo
import com.manish.liquidlauncher.model.LiquidThemeConfig

fun LiquidThemeConfig.accentColor(): Color {
    return Color.hsv(accentHue.coerceIn(0f, 360f), 0.82f, 1f)
}

@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    config: LiquidThemeConfig,
    cornerRadius: Dp = config.cornerRadiusDp.dp,
    tintOverride: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val accent = tintOverride ?: config.accentColor()

    // Deep multi-layered refractive glass body
    val bodyGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = (config.refractionOpacity * 0.75f + 0.08f).coerceAtMost(0.35f)),
            accent.copy(alpha = (config.refractionOpacity * 0.55f).coerceAtMost(0.28f)),
            Color(0xFF060D18).copy(alpha = 0.72f),
            accent.copy(alpha = (config.refractionOpacity * 0.45f).coerceAtMost(0.24f))
        )
    )

    // 3D convex bevel rim (bright top-left light entry, soft bottom caustic exit)
    val rimGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = (0.72f * config.glowIntensity).coerceIn(0.2f, 0.95f)),
            accent.copy(alpha = (0.45f * config.glowIntensity).coerceIn(0.15f, 0.85f)),
            Color.White.copy(alpha = 0.06f),
            accent.copy(alpha = (0.55f * config.glowIntensity).coerceIn(0.15f, 0.85f))
        ),
        start = Offset(0f, 0f),
        end = Offset(400f, 400f)
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = accent.copy(alpha = 0.5f),
                spotColor = accent.copy(alpha = 0.65f)
            )
            .clip(shape)
            .background(bodyGradient)
            .border(width = 1.15.dp, brush = rimGradient, shape = shape),
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

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.84f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dropletScale"
    )

    val rawBitmap = remember(app.packageName) { drawableToBitmap(app.icon) }
    val imageBitmap = remember(app.packageName) { rawBitmap.asImageBitmap() }
    val appTint = remember(app.packageName) { extractDominantColor(rawBitmap) }
    val corner = (iconBoxSize.value * 0.34f).dp
    val shape = RoundedCornerShape(corner)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(iconBoxSize)
                .shadow(
                    elevation = 10.dp,
                    shape = shape,
                    ambientColor = appTint,
                    spotColor = appTint
                )
                .clip(shape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            appTint.copy(alpha = 0.42f),
                            Color(0xFF0A1322).copy(alpha = 0.78f),
                            appTint.copy(alpha = 0.55f)
                        ),
                        center = Offset(30f, 30f),
                        radius = 130f
                    )
                )
                .border(
                    width = 1.25.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.82f),
                            appTint.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.12f),
                            appTint.copy(alpha = 0.75f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(160f, 160f)
                    ),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            // App glyph in center
            Image(
                bitmap = imageBitmap,
                contentDescription = app.label,
                modifier = Modifier
                    .size(iconBoxSize * 0.62f)
                    .clip(RoundedCornerShape(13.dp))
            )

            // Curved liquid meniscus gloss overlay (replaces the flat grey rectangle)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val glossPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height * 0.32f)
                    quadraticBezierTo(
                        size.width * 0.5f,
                        size.height * 0.16f,
                        0f,
                        size.height * 0.42f
                    )
                    close()
                }
                drawPath(
                    path = glossPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = app.label,
                color = Color.White.copy(alpha = 0.94f),
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LiquidFolderItem(
    title: String,
    apps: List<AppInfo>,
    config: LiquidThemeConfig,
    onClick: () -> Unit
) {
    val accent = config.accentColor()
    val corner = 21.dp
    val shape = RoundedCornerShape(corner)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .shadow(10.dp, shape, ambientColor = accent, spotColor = accent)
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.22f),
                            accent.copy(alpha = 0.28f),
                            Color(0xFF07101E).copy(alpha = 0.80f)
                        )
                    )
                )
                .border(
                    1.25.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            accent.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val preview = apps.take(4)
                for (row in 0..1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (col in 0..1) {
                            val idx = row * 2 + col
                            if (idx < preview.size) {
                                val bmp = remember(preview[idx].packageName) {
                                    drawableToBitmap(preview[idx].icon).asImageBitmap()
                                }
                                Image(
                                    bitmap = bmp,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(19.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                            } else {
                                Spacer(modifier = Modifier.size(19.dp))
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.94f),
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun extractDominantColor(bitmap: Bitmap): Color {
    return try {
        val scaled = Bitmap.createScaledBitmap(bitmap, 16, 16, true)
        var rSum = 0L
        var gSum = 0L
        var bSum = 0L
        var count = 0L
        for (x in 2 until 14) {
            for (y in 2 until 14) {
                val pixel = scaled.getPixel(x, y)
                val alpha = (pixel shr 24) and 0xFF
                if (alpha > 140) {
                    val r = (pixel shr 16) and 0xFF
                    val g = (pixel shr 8) and 0xFF
                    val b = pixel and 0xFF
                    // Skip near-white and near-black background pixels
                    if (r + g + b in 75..680) {
                        rSum += r
                        gSum += g
                        bSum += b
                        count++
                    }
                }
            }
        }
        if (count > 0) {
            Color(
                red = (rSum / count).toInt().coerceIn(40, 255),
                green = (gSum / count).toInt().coerceIn(40, 255),
                blue = (bSum / count).toInt().coerceIn(40, 255)
            )
        } else {
            Color(0xFF38BDF8)
        }
    } catch (_: Exception) {
        Color(0xFF38BDF8)
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
