package com.manish.liquidlauncher.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.liquidlauncher.model.LiquidThemeConfig
import com.manish.liquidlauncher.ui.components.LiquidGlassSurface
import com.manish.liquidlauncher.ui.components.accentColor
import com.manish.liquidlauncher.viewmodel.LauncherViewModel

@Composable
fun ThemeEngineSheet(viewModel: LauncherViewModel) {
    val config = viewModel.themeConfig
    val accent = config.accentColor()

    AnimatedVisibility(
        visible = viewModel.isThemeEngineOpen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { viewModel.isThemeEngineOpen = false }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            LiquidGlassSurface(
                config = config,
                cornerRadius = 30.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "10. Liquid Theme Engine",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            IconButton(onClick = { viewModel.updateTheme(LiquidThemeConfig()) }) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Reset",
                                    tint = accent
                                )
                            }
                            IconButton(onClick = { viewModel.isThemeEngineOpen = false }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Accent Hue Slider
                    ThemeSliderControl(
                        label = "Accent Hue (${config.accentHue.toInt()}°)",
                        value = config.accentHue,
                        range = 0f..360f,
                        accent = accent,
                        onValueChange = { viewModel.updateTheme(config.copy(accentHue = it)) }
                    )

                    // Refraction Opacity Slider
                    ThemeSliderControl(
                        label = "Glass Refraction Density",
                        value = config.refractionOpacity,
                        range = 0.08f..0.48f,
                        accent = accent,
                        onValueChange = { viewModel.updateTheme(config.copy(refractionOpacity = it)) }
                    )

                    // Edge Glow Intensity Slider
                    ThemeSliderControl(
                        label = "Specular Glow Intensity",
                        value = config.glowIntensity,
                        range = 0.2f..1.0f,
                        accent = accent,
                        onValueChange = { viewModel.updateTheme(config.copy(glowIntensity = it)) }
                    )

                    // Squircle Curvature Slider
                    ThemeSliderControl(
                        label = "Liquid Squircle Radius",
                        value = config.cornerRadiusDp,
                        range = 12f..34f,
                        accent = accent,
                        onValueChange = { viewModel.updateTheme(config.copy(cornerRadiusDp = it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSliderControl(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    accent: Color,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}
