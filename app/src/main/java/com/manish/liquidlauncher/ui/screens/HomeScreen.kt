package com.manish.liquidlauncher.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.manish.liquidlauncher.ui.components.LiquidAppIconItem
import com.manish.liquidlauncher.ui.components.LiquidGlassSurface
import com.manish.liquidlauncher.ui.components.accentColor
import com.manish.liquidlauncher.viewmodel.LauncherViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    onPickWallpaper: () -> Unit
) {
    val context = LocalContext.current
    val config = viewModel.themeConfig
    val accent = config.accentColor()

    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    val batteryLevel = remember { viewModel.getBatteryLevel(context) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            currentDate = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(now)
            delay(15_000L)
        }
    }

        Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF040811))
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    when {
                        dragAmount < -28f -> viewModel.isDrawerOpen = true
                        dragAmount > 28f -> viewModel.isControlCenterOpen = true
                    }
                }
            }
    ) {
        // 02. Wallpaper Pack: Custom Gallery Image OR Procedural Liquid Ocean Waves
        if (viewModel.customWallpaperUri != null) {
            AsyncImage(
                model = viewModel.customWallpaperUri,
                contentDescription = "Custom Wallpaper",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.28f))
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF050B16),
                            Color(0xFF091B2E),
                            Color(0xFF040811)
                        )
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.32f), Color.Transparent),
                        center = Offset(size.width * 0.25f, size.height * 0.22f),
                        radius = size.width * 0.75f
                    ),
                    center = Offset(size.width * 0.25f, size.height * 0.22f),
                    radius = size.width * 0.75f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6366F1).copy(alpha = 0.26f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.72f),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.72f),
                    radius = size.width * 0.8f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Controls + 04. Liquid Search Bar
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiquidGlassSurface(
                        config = config,
                        cornerRadius = 50.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clickable { viewModel.isDrawerOpen = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = "Search",
                                    tint = accent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Search apps & fluid grid...",
                                    color = Color.White.copy(alpha = 0.72f),
                                    fontSize = 14.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Rounded.Apps,
                                contentDescription = "Open Drawer",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Quick Gallery Wallpaper Import Button
                    LiquidGlassSurface(
                        config = config,
                        cornerRadius = 50.dp,
                        modifier = Modifier.size(50.dp)
                    ) {
                        IconButton(onClick = onPickWallpaper) {
                            Icon(
                                imageVector = Icons.Rounded.Wallpaper,
                                contentDescription = "Import Wallpaper",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 10. Theme Engine Trigger Button
                    LiquidGlassSurface(
                        config = config,
                        cornerRadius = 50.dp,
                        modifier = Modifier.size(50.dp)
                    ) {
                        IconButton(onClick = { viewModel.isThemeEngineOpen = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Tune,
                                contentDescription = "Theme Engine",
                                tint = accent
                            )
                        }
                    }
                }

                // 03. Widget Pack: Clock, Weather & Liquid Battery Capsule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LiquidGlassSurface(
                        config = config,
                        modifier = Modifier
                            .weight(1.45f)
                            .height(118.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentDate,
                                    color = accent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Rounded.WaterDrop,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = currentTime,
                                color = Color.White,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Liquid Battery Tank Widget
                    LiquidGlassSurface(
                        config = config,
                        modifier = Modifier
                            .weight(0.85f)
                            .height(118.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .fillMaxHeight(batteryLevel / 100f)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                accent.copy(alpha = 0.45f),
                                                accent.copy(alpha = 0.18f)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Bolt,
                                    contentDescription = "Battery",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "$batteryLevel%",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "FLUID CELL",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // 04. Natural Fluid Grid (Home Screen Apps)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 18.dp)
            ) {
                items(viewModel.homeApps, key = { it.packageName }) { app ->
                    LiquidAppIconItem(
                        app = app,
                        config = config,
                        onClick = { viewModel.launchApp(context, app.packageName) }
                    )
                }
            }

            // 04. Viscous Liquid Floating Dock
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .size(38.dp, 4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.45f))
                        .clickable { viewModel.isDrawerOpen = true }
                )

                LiquidGlassSurface(
                    config = config,
                    cornerRadius = 32.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        viewModel.dockApps.forEach { app ->
                            LiquidAppIconItem(
                                app = app,
                                config = config,
                                showLabel = false,
                                iconBoxSize = 56.dp,
                                onClick = { viewModel.launchApp(context, app.packageName) }
                            )
                        }
                    }
                }
            }
        }
    }
}
