package com.manish.liquidlauncher.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

    var homeApps by mutableStateOf<List<AppInfo>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
    var isDrawerOpen by mutableStateOf(false)
    var isThemeEngineOpen by mutableStateOf(false)
    var isControlCenterOpen by mutableStateOf(false)
    var customWallpaperUri by mutableStateOf<Uri?>(null)
    var themeConfig by mutableStateOf(LiquidThemeConfig())

    // 06. System Pack Hardware States
    var isTorchOn by mutableStateOf(false)
    var isDndActive by mutableStateOf(false)
    var isExtraDimActive by mutableStateOf(false)
    var isPowerSavingActive by mutableStateOf(false)
    var brightnessLevel by mutableFloatStateOf(0.55f)
    var mediaVolumeLevel by mutableFloatStateOf(0.45f)

    init {
        loadInstalledApps()
        syncAudioVolume()
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
                homeApps = apps.drop(4).take(12)
            }
        }
    }

    fun launchApp(context: Context, packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            isDrawerOpen = false
            isControlCenterOpen = false
        }
    }

    fun getBatteryLevel(context: Context): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) (level * 100) / scale else 85
    }

    fun getCarrierName(context: Context): String {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val name = tm?.networkOperatorName
            if (!name.isNullOrBlank()) name else "VodafoneIdea"
        } catch (_: Exception) {
            "VodafoneIdea"
        }
    }

    fun toggleTorch(context: Context) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            if (cameraId != null) {
                val nextState = !isTorchOn
                cameraManager.setTorchMode(cameraId, nextState)
                isTorchOn = nextState
            }
        } catch (_: Exception) {
            isTorchOn = !isTorchOn
        }
    }

    private fun syncAudioVolume() {
        try {
            val audio = getApplication<Application>().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
            val current = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
            mediaVolumeLevel = current.toFloat() / max.toFloat()
        } catch (_: Exception) { }
    }

    fun setMediaVolume(context: Context, fraction: Float) {
        mediaVolumeLevel = fraction.coerceIn(0f, 1f)
        try {
            val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val target = (mediaVolumeLevel * max).toInt()
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        } catch (_: Exception) { }
    }

    fun openSystemPanel(context: Context, action: String) {
        try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        }
    }

    fun openConnectivityPanel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            openSystemPanel(context, Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
        } else {
            openSystemPanel(context, Settings.ACTION_WIFI_SETTINGS)
        }
    }

    fun updateTheme(newConfig: LiquidThemeConfig) {
        themeConfig = newConfig
    }
}
