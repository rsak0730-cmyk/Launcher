package com.manish.liquidlauncher.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

data class LiquidThemeConfig(
    val accentHue: Float = 195f,          // Default Deep Ocean Cyan
    val refractionOpacity: Float = 0.22f, // Glass surface density
    val glowIntensity: Float = 0.65f,     // Specular edge highlight
    val cornerRadiusDp: Float = 26f       // Liquid squircle curvature
)
