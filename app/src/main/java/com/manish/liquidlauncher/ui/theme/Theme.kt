package com.manish.liquidlauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LiquidDarkScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    secondary = Color(0xFF818CF8),
    background = Color(0xFF060B14),
    surface = Color(0xFF0D1726),
    onPrimary = Color.White,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

@Composable
fun LiquidGlassTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LiquidDarkScheme,
        content = content
    )
}
