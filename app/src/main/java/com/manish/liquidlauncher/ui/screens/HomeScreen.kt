package com.manish.liquidlauncher.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.manish.liquidlauncher.model.AppInfo
import com.manish.liquidlauncher.ui.components.LiquidAppIconItem
import com.manish.liquidlauncher.ui.components.LiquidFolderItem
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
    var openedFolderTitle by remember { mutableStateOf<String?>(null) }
    var openedFolderApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    val batteryLevel = remember { viewModel.getBatteryLevel(context) }

    // Auto-grouped Liquid Folders
    val socialApps = remember(viewModel.installedApps) {
        viewModel.installedApps.filter {
            it.packageName.contains("whatsapp") ||
                it.packageName.contains("instagram") ||
                it.packageName.contains("telegram") ||
                it.packageName.contains("discord") ||
                it.packageName.contains("snapchat")
        }
    }
    val googleApps = remember(viewModel.installedApps) {
        viewModel.installedApps.filter {
            it.packageName.contains("google") ||
                it.packageName.contains("chrome") ||
                it.packageName.contains("youtube") ||
                it.packageName.contains("maps")
        }
    }

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
        // Wallpaper Layer
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
                    .background(Color.Black.copy(alpha = 0.25f))
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF040A14),
                            Color(0xFF091B2E),
                            Color(0xFF040811)
                        )
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.34f), Color.Transparent),
                        center = Offset(size.width * 0.2f, size.height * 0.2f),
                        radius = size.width * 0.75f
                    ),
                    center = Offset(size.width * 0.2f, size.height * 0.2f),
                    radius = size.width * 0.75f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6366F1).copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.75f),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.75f),
                    radius = size.width * 0.8f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Controls + Search Bar + 1-Tap Launcher Switcher
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LiquidGlassSurface(
                        config = config,
                        cornerRadius = 50.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clickable { viewModel.isDrawerOpen = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search",
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Search apps...",
                                color = Color.White.copy(alpha = 0.75f),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // 1-Tap Switch Between One UI Home & Liquid Launcher
                    LiquidGlassSurface(
                        config = config,
                        cornerRadius = 50.dp,
                        modifier = Modifier.size(46.dp)
                    ) {
                        IconButton(onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                viewModel.openSystemPanel(context, Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.SwapHoriz,
                                contentDescription = "Switch Default Launcher",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Gallery Wallpaper Picker
                    LiquidGlassSurface(
                        config = config,
                        cornerRadius = 50.dp,
                        modifier = Modifier.size(46.dp)
                    ) {
                        IconButton(onClick = onPickWallpaper) {
                            Icon(
                                imageVector = Icons.Rounded.Wallpaper,
                                contentDescription = "Wallpaper",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    // Theme Engine
                    LiquidGlassSurface(
                        config = config,
                        cornerRadius = 50.dp,
                        modifier = Modifier.size(46.dp)
                    ) {
                        IconButton(onClick = { viewModel.isThemeEngineOpen = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Tune,
                                contentDescription = "Theme Engine",
                                tint = accent,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                // 03. Clock & Liquid Battery Capsule Widgets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LiquidGlassSurface(
                        config = config,
                        modifier = Modifier
                            .weight(1.45f)
                            .height(114.dp)
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
                                    fontFamily = FontFamily.SansSerif,
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
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    LiquidGlassSurface(
                        config = config,
                        modifier = Modifier
                            .weight(0.85f)
                            .height(114.dp)
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
                                                accent.copy(alpha = 0.52f),
                                                accent.copy(alpha = 0.16f)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Bolt,
                                    contentDescription = "Battery",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "$batteryLevel%",
                                    color = Color.White,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "FLUID CELL",
                                    color = Color.White.copy(alpha = 0.72f),
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Home Grid: Liquid Folders + App-Specific Liquid Icons
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                if (socialApps.isNotEmpty()) {
                    item(key = "folder_social") {
                        LiquidFolderItem(
                            title = "Social",
                            apps = socialApps,
                            config = config,
                            onClick = {
                                openedFolderTitle = "Social"
                                openedFolderApps = socialApps
                            }
                        )
                    }
                }
                if (googleApps.isNotEmpty()) {
                    item(key = "folder_google") {
                        LiquidFolderItem(
                            title = "Google",
                            apps = googleApps,
                            config = config,
                            onClick = {
                                openedFolderTitle = "Google"
                                openedFolderApps = googleApps
                            }
                        )
                    }
                }
                items(viewModel.homeApps.take(10), key = { it.packageName }) { app ->
                    LiquidAppIconItem(
                        app = app,
                        config = config,
                        onClick = { viewModel.launchApp(context, app.packageName) }
                    )
                }
            }

            // Viscous Liquid Floating Dock
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

        // Liquid Folder Pop-Up Modal
        AnimatedVisibility(
            visible = openedFolderTitle != null,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.85f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF02060D).copy(alpha = 0.84f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { openedFolderTitle = null }
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                LiquidGlassSurface(
                    config = config,
                    cornerRadius = 32.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) {}
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = openedFolderTitle ?: "",
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.heightIn(max = 280.dp)
                        ) {
                            items(openedFolderApps, key = { it.packageName }) { app ->
                                LiquidAppIconItem(
                                    app = app,
                                    config = config,
                                    onClick = {
                                        openedFolderTitle = null
                                        viewModel.launchApp(context, app.packageName)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
