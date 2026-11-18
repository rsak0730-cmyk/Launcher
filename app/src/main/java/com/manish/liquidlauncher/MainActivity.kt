package com.manish.liquidlauncher

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.manish.liquidlauncher.ui.screens.AppDrawerSheet
import com.manish.liquidlauncher.ui.screens.HomeScreen
import com.manish.liquidlauncher.ui.screens.ThemeEngineSheet
import com.manish.liquidlauncher.ui.theme.LiquidGlassTheme
import com.manish.liquidlauncher.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiquidGlassTheme {
                LiquidLauncherRoot()
            }
        }
    }
}

@Composable
fun LiquidLauncherRoot(viewModel: LauncherViewModel = viewModel()) {
    // 02. Wallpaper Pack: Custom Import from Android Gallery
    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.customWallpaperUri = uri
        }
    }

    BackHandler(enabled = true) {
        when {
            viewModel.isThemeEngineOpen -> viewModel.isThemeEngineOpen = false
            viewModel.isDrawerOpen -> {
                viewModel.isDrawerOpen = false
                viewModel.searchQuery = ""
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen(
            viewModel = viewModel,
            onPickWallpaper = { galleryPicker.launch("image/*") }
        )
        AppDrawerSheet(viewModel = viewModel)
        ThemeEngineSheet(viewModel = viewModel)
    }
}
