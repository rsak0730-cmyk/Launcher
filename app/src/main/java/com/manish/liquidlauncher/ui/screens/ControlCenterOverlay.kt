package com.manish.liquidlauncher.ui.screens

import android.app.Activity
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.liquidlauncher.model.LiquidThemeConfig
import com.manish.liquidlauncher.ui.components.LiquidGlassSurface
import com.manish.liquidlauncher.ui.components.accentColor
import com.manish.liquidlauncher.viewmodel.LauncherViewModel

private data class QuickTileSpec(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val isActive: Boolean,
    val onClick: () -> Unit
)

@Composable
fun ControlCenterOverlay(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val config = viewModel.themeConfig
    val accent = config.accentColor()
    val carrier = remember { viewModel.getCarrierName(context) }
    val battery = remember(viewModel.isControlCenterOpen) { viewModel.getBatteryLevel(context) }

    val quickTiles = listOf(
        QuickTileSpec("Portrait", null, Icons.Rounded.ScreenLockPortrait, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_DISPLAY_SETTINGS)
        },
        QuickTileSpec("Flight mode", null, Icons.Rounded.Flight, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_AIRPLANE_MODE_SETTINGS)
        },
        QuickTileSpec("Torch", null, Icons.Rounded.FlashlightOn, viewModel.isTorchOn) {
            viewModel.toggleTorch(context)
        },
        QuickTileSpec("Mobile data", null, Icons.Rounded.SwapVert, true) {
            viewModel.openConnectivityPanel(context)
        },
        QuickTileSpec("Mobile Hotspot", null, Icons.Rounded.WifiTethering, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_WIRELESS_SETTINGS)
        },
        QuickTileSpec("Power saving", null, Icons.Rounded.BatteryChargingFull, viewModel.isPowerSavingActive) {
            viewModel.isPowerSavingActive = !viewModel.isPowerSavingActive
            viewModel.openSystemPanel(context, Settings.ACTION_BATTERY_SAVER_SETTINGS)
        },
        QuickTileSpec("Location", null, Icons.Rounded.LocationOn, true) {
            viewModel.openSystemPanel(context, Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        },
        QuickTileSpec("Quick Share", "No one", Icons.Rounded.Share, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_BLUETOOTH_SETTINGS)
        },
        QuickTileSpec("Eye comfort", null, Icons.Rounded.WbSunny, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_DISPLAY_SETTINGS)
        },
        QuickTileSpec("Do not disturb", null, Icons.Rounded.DoNotDisturbOn, viewModel.isDndActive) {
            viewModel.isDndActive = !viewModel.isDndActive
        },
        QuickTileSpec("Scan QR code", null, Icons.Rounded.QrCodeScanner, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_WIFI_SETTINGS)
        },
        QuickTileSpec("Battery protect", "Off", Icons.Rounded.BatteryStd, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_BATTERY_SAVER_SETTINGS)
        },
        QuickTileSpec("Wi-Fi Calling", null, Icons.Rounded.WifiCalling3, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_WIRELESS_SETTINGS)
        },
        QuickTileSpec("Screenshot", null, Icons.Rounded.Screenshot, false) {
            viewModel.isControlCenterOpen = false
        },
        QuickTileSpec("Dolby Atmos", null, Icons.Rounded.SurroundSound, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_SOUND_SETTINGS)
        },
        QuickTileSpec("Emergency", null, Icons.Rounded.Sos, false) {
            viewModel.openSystemPanel(context, Settings.ACTION_SETTINGS)
        },
        QuickTileSpec("Song search", null, Icons.Rounded.MusicNote, false) {
            viewModel.isControlCenterOpen = false
            viewModel.isDrawerOpen = true
        },
        QuickTileSpec("Extra dim", null, Icons.Rounded.BrightnessLow, viewModel.isExtraDimActive) {
            viewModel.isExtraDimActive = !viewModel.isExtraDimActive
        }
    )

    AnimatedVisibility(
        visible = viewModel.isControlCenterOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF030711).copy(alpha = 0.98f),
                            Color(0xFF071426).copy(alpha = 0.98f)
                        )
                    )
                )
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < -26f) {
                            viewModel.isControlCenterOpen = false
                        }
                    }
                }
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Top Carrier & Utility Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = carrier,
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "VoLTE 4G",
                            color = accent,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(accent.copy(alpha = 0.25f))
                                .border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚡ $battery%",
                                color = Color.White,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Edit / Close / System Settings Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            viewModel.isControlCenterOpen = false
                            viewModel.isThemeEngineOpen = true
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = "Customize",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.isControlCenterOpen = false },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Rounded.PowerSettingsNew,
                            contentDescription = "Close Panel",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.openSystemPanel(context, Settings.ACTION_SETTINGS) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Settings,
                            contentDescription = "System Settings",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 2. Dual Wide Connectivity Pills (Wi-Fi & Bluetooth)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ConnectivityPill(
                        title = "Wi-Fi",
                        icon = Icons.Rounded.Wifi,
                        isActive = true,
                        config = config,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openConnectivityPanel(context) }
                    )
                    ConnectivityPill(
                        title = "Bluetooth",
                        icon = Icons.Rounded.Bluetooth,
                        isActive = false,
                        config = config,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openSystemPanel(context, Settings.ACTION_BLUETOOTH_SETTINGS) }
                    )
                }

                // 3. Main Liquid Glass 4x5 Quick Settings Matrix
                LiquidGlassSurface(
                    config = config,
                    cornerRadius = 30.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(quickTiles) { tile ->
                                LiquidQuickTileItem(tile = tile, config = config)
                            }
                        }

                        // Page indicator capsule
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 6.dp)
                                .size(26.dp, 4.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.7f))
                        )
                    }
                }

                // 4. Dual Brightness & Media Volume Liquid Card
                LiquidGlassSurface(
                    config = config,
                    cornerRadius = 28.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Brightness Slider Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LiquidSliderTrack(
                                value = viewModel.brightnessLevel,
                                icon = Icons.Rounded.WbSunny,
                                accent = accent,
                                modifier = Modifier.weight(1f),
                                onValueChange = { newVal ->
                                    viewModel.brightnessLevel = newVal
                                    val window = (context as? Activity)?.window
                                    if (window != null) {
                                        val lp = window.attributes
                                        lp.screenBrightness = newVal.coerceIn(0.05f, 1.0f)
                                        window.attributes = lp
                                    }
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (viewModel.isExtraDimActive) accent
                                        else Color.White.copy(alpha = 0.14f)
                                    )
                                    .clickable { viewModel.isExtraDimActive = !viewModel.isExtraDimActive },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DarkMode,
                                    contentDescription = "Night Dim",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Live Media Volume Slider Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LiquidSliderTrack(
                                value = viewModel.mediaVolumeLevel,
                                icon = Icons.Rounded.MusicNote,
                                accent = accent,
                                modifier = Modifier.weight(1f),
                                onValueChange = { viewModel.setMediaVolume(context, it) }
                            )
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.9f))
                                    .clickable {
                                        val next = if (viewModel.mediaVolumeLevel > 0f) 0f else 0.5f
                                        viewModel.setMediaVolume(context, next)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (viewModel.mediaVolumeLevel > 0f) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                                    contentDescription = "Volume Toggle",
                                    tint = Color(0xFF060B14),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom Swipe-Up Handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 2.dp)
                        .size(44.dp, 4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.45f))
                        .clickable { viewModel.isControlCenterOpen = false }
                )
            }
        }
    }
}

@Composable
private fun ConnectivityPill(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    config: LiquidThemeConfig,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accent = config.accentColor()
    LiquidGlassSurface(
        config = config,
        cornerRadius = 50.dp,
        modifier = modifier
            .height(62.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isActive) accent else Color.White.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = title,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LiquidQuickTileItem(
    tile: QuickTileSpec,
    config: LiquidThemeConfig
) {
    val accent = config.accentColor()
    val scale by animateFloatAsState(
        targetValue = if (tile.isActive) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tileBounce"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = tile.onClick
            )
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (tile.isActive) {
                        Brush.radialGradient(
                            colors = listOf(Color.White, accent.copy(alpha = 0.85f))
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.16f),
                                Color.White.copy(alpha = 0.06f)
                            )
                        )
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (tile.isActive) Color.White else Color.White.copy(alpha = 0.22f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tile.icon,
                contentDescription = tile.title,
                tint = if (tile.isActive) Color(0xFF040A14) else Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = tile.title,
            color = Color.White.copy(alpha = 0.92f),
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 12.sp,
            overflow = TextOverflow.Ellipsis
        )

        if (tile.subtitle != null) {
            Text(
                text = tile.subtitle,
                color = accent.copy(alpha = 0.85f),
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LiquidSliderTrack(
    value: Float,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.14f))
            .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(value.coerceIn(0.14f, 1f))
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(accent.copy(alpha = 0.85f), Color.White.copy(alpha = 0.95f))
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF060B14),
                modifier = Modifier.size(18.dp)
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}
