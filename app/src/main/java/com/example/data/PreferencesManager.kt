package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_config", Context.MODE_PRIVATE)

    fun getActionBegin(actionId: Int, defaultVal: Long): Long {
        return prefs.getLong("ActionRangebgin$actionId", defaultVal)
    }

    fun setActionBegin(actionId: Int, value: Long) {
        prefs.edit().putLong("ActionRangebgin$actionId", value).apply()
    }

    fun getActionEnd(actionId: Int, defaultVal: Long): Long {
        return prefs.getLong("ActionRangeEnd$actionId", defaultVal)
    }

    fun setActionEnd(actionId: Int, value: Long) {
        prefs.edit().putLong("ActionRangeEnd$actionId", value).apply()
    }

    var isLoop: Boolean
        get() = prefs.getBoolean("PlayisLoop", true)
        set(value) = prefs.edit().putBoolean("PlayisLoop", value).apply()

    var autoColorIntensity: Float
        get() = prefs.getFloat("AutoColor_intensity", 20f)
        set(value) = prefs.edit().putFloat("AutoColor_intensity", value).apply()

    var autoColorDiameter: Float
        get() = prefs.getFloat("AutoColor_diameter", 25f)
        set(value) = prefs.edit().putFloat("AutoColor_diameter", value).apply()

    var autoColorX: Float
        get() = prefs.getFloat("AutoColor_X", 50f)
        set(value) = prefs.edit().putFloat("AutoColor_X", value).apply()

    var autoColorY: Float
        get() = prefs.getFloat("AutoColor_Y", 50f)
        set(value) = prefs.edit().putFloat("AutoColor_Y", value).apply()

    var monitorTargetX: Int
        get() = prefs.getInt("MonitorTargetX", 540)
        set(value) = prefs.edit().putInt("MonitorTargetX", value).apply()

    var monitorTargetY: Int
        get() = prefs.getInt("MonitorTargetY", 960)
        set(value) = prefs.edit().putInt("MonitorTargetY", value).apply()

    var colorMode: Int
        get() = prefs.getInt("ColorMode", 2)
        set(value) = prefs.edit().putInt("ColorMode", value).apply()

    var replacementMode: String
        get() = prefs.getString("ReplacementMode", "VIDEO") ?: "VIDEO"
        set(value) = prefs.edit().putString("ReplacementMode", value).apply()

    var rtmpUrl: String
        get() = prefs.getString("RtmpUrl", "rtmp://live.stream.example/live/room1") ?: ""
        set(value) = prefs.edit().putString("RtmpUrl", value).apply()

    var videoPath: String
        get() = prefs.getString("VideoPath", "demo_virtual_feed.mp4") ?: "demo_virtual_feed.mp4"
        set(value) = prefs.edit().putString("VideoPath", value).apply()

    var replaceCameraActive: Boolean
        get() = prefs.getBoolean("replace_camera", false)
        set(value) = prefs.edit().putBoolean("replace_camera", value).apply()

    var frontPreviewActive: Boolean
        get() = prefs.getBoolean("front_preview", true)
        set(value) = prefs.edit().putBoolean("front_preview", value).apply()

    var aspectCorrectActive: Boolean
        get() = prefs.getBoolean("aspect_correct", true)
        set(value) = prefs.edit().putBoolean("aspect_correct", value).apply()

    var colorInjectActive: Boolean
        get() = prefs.getBoolean("color_inject", false)
        set(value) = prefs.edit().putBoolean("color_inject", value).apply()

    var floatingWindowActive: Boolean
        get() = prefs.getBoolean("floating_window", false)
        set(value) = prefs.edit().putBoolean("floating_window", value).apply()

    fun resetToDefaults() {
        ActionPresets.DEFAULT_ACTIONS.forEach { action ->
            setActionBegin(action.id, action.defaultStartUs)
            setActionEnd(action.id, action.defaultEndUs)
        }
        isLoop = true
        autoColorIntensity = 20f
        autoColorDiameter = 25f
        autoColorX = 50f
        autoColorY = 50f
        monitorTargetX = 540
        monitorTargetY = 960
        colorMode = 2
    }
}
