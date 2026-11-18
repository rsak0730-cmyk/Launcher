package com.manish.liquidlauncher.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.manish.liquidlauncher.ui.components.LiquidAppIconItem
import com.manish.liquidlauncher.ui.components.LiquidGlassSurface
import com.manish.liquidlauncher.ui.components.accentColor
import com.manish.liquidlauncher.viewmodel.LauncherViewModel

@Composable
fun AppDrawerSheet(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val config = viewModel.themeConfig
    val accent = config.accentColor()

    val filteredApps = remember(viewModel.installedApps, viewModel.searchQuery) {
        if (viewModel.searchQuery.isBlank()) {
            viewModel.installedApps
        } else {
            viewModel.installedApps.filter {
                it.label.contains(viewModel.searchQuery, ignoreCase = true)
            }
        }
    }

    AnimatedVisibility(
        visible = viewModel.isDrawerOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 3 })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF040914).copy(alpha = 0.92f))
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 05. Natural Fluid Grid Search Header
                LiquidGlassSurface(
                    config = config,
                    cornerRadius = 28.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = accent
                        )
                        TextField(
                            value = viewModel.searchQuery,
                            onValueChange = { viewModel.searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search ${viewModel.installedApps.size} apps...",
                                    color = Color.White.copy(alpha = 0.55f)
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = accent
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            if (viewModel.searchQuery.isNotEmpty()) {
                                viewModel.searchQuery = ""
                            } else {
                                viewModel.isDrawerOpen = false
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close Drawer",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 05. Natural Fluid Grid App List
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        LiquidAppIconItem(
                            app = app,
                            config = config,
                            onClick = { viewModel.launchApp(context, app.packageName) }
                        )
                    }
                }
            }
        }
    }
}
