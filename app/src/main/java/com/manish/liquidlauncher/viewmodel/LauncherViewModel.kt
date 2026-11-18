package com.manish.liquidlauncher.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manish.liquidlauncher.model.AppInfo
import com.manish.liquidlauncher.model.LiquidThemeConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    var installedApps by mutableStateOf<List<AppInfo>>(emptyList())
        private set

    var dockApps by mutableStateOf<List<AppInfo>>(emptyList())
        private set

    var homePages by mutableStateOf<List<List<AppInfo>>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
    var isDrawerOpen by mutableStateOf(false)
    var isThemeEngineOpen by mutableStateOf(false)
    var customWallpaperUri by mutableStateOf<Uri?>(null)
    var themeConfig by mutableStateOf(LiquidThemeConfig())

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolved = pm.queryIntentActivities(mainIntent, 0)
            val selfPackage = getApplication<Application>().packageName

            val apps = resolved
                .filter { it.activityInfo.packageName != selfPackage }
                .map { resolveInfo ->
                    AppInfo(
                        label = resolveInfo.loadLabel(pm).toString(),
                        packageName = resolveInfo.activityInfo.packageName,
                        icon = resolveInfo.loadIcon(pm)
                    )
                }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase() }

            withContext(Dispatchers.Main) {
                installedApps = apps
                dockApps = apps.take(4)

                // Page 1 holds 10 apps alongside 2 folders (12 slots = 3x4 grid below widgets)
                // Page 2 & Page 3 hold 16 apps each (4x4 full One UI grid)
                val remaining = apps.drop(4)
                val page1 = remaining.take(10)
                val page2 = remaining.drop(10).take(16)
                val page3 = remaining.drop(26).take(16)

                homePages = listOf(page1, page2, page3).filter { it.isNotEmpty() }.ifEmpty {
                    listOf(emptyList())
                }
            }
        }
    }

    fun launchApp(context: Context, packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            isDrawerOpen = false
        }
    }

    fun getBatteryLevel(context: Context): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) (level * 100) / scale else 85
    }

    fun openDefaultLauncherPicker(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        }
    }

    fun updateTheme(newConfig: LiquidThemeConfig) {
        themeConfig = newConfig
    }
}
