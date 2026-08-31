package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.VLiveApp
import com.example.data.ActionPresets
import com.example.data.ColorMode
import com.example.data.FacialAction
import com.example.data.PreferencesManager
import com.example.data.ReplacementMode
import com.example.root.InjectionStatus
import com.example.root.NativeCameraInjector
import com.example.root.ResidualScanResult
import com.example.service.FloatService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VLiveViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: PreferencesManager = (application as VLiveApp).preferencesManager
    val injector: NativeCameraInjector = (application as VLiveApp).cameraInjector

    private val _injectionStatus = MutableStateFlow(InjectionStatus())
    val injectionStatus: StateFlow<InjectionStatus> = _injectionStatus.asStateFlow()

    private val _deployProgress = MutableStateFlow<String?>(null)
    val deployProgress: StateFlow<String?> = _deployProgress.asStateFlow()

    private val _residualResult = MutableStateFlow(ResidualScanResult())
    val residualResult: StateFlow<ResidualScanResult> = _residualResult.asStateFlow()

    private val _replacementMode = MutableStateFlow(
        if (prefs.replacementMode == "RTMP") ReplacementMode.RTMP else ReplacementMode.VIDEO
    )
    val replacementMode: StateFlow<ReplacementMode> = _replacementMode.asStateFlow()

    private val _videoPath = MutableStateFlow(prefs.videoPath)
    val videoPath: StateFlow<String> = _videoPath.asStateFlow()

    private val _rtmpUrl = MutableStateFlow(prefs.rtmpUrl)
    val rtmpUrl: StateFlow<String> = _rtmpUrl.asStateFlow()

    private val _isReplacing = MutableStateFlow(prefs.replaceCameraActive)
    val isReplacing: StateFlow<Boolean> = _isReplacing.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLoop = MutableStateFlow(prefs.isLoop)
    val isLoop: StateFlow<Boolean> = _isLoop.asStateFlow()

    private val _frontPreview = MutableStateFlow(prefs.frontPreviewActive)
    val frontPreview: StateFlow<Boolean> = _frontPreview.asStateFlow()

    private val _aspectCorrect = MutableStateFlow(prefs.aspectCorrectActive)
    val aspectCorrect: StateFlow<Boolean> = _aspectCorrect.asStateFlow()

    private val _colorInject = MutableStateFlow(prefs.colorInjectActive)
    val colorInject: StateFlow<Boolean> = _colorInject.asStateFlow()

    private val _floatingWindow = MutableStateFlow(prefs.floatingWindowActive)
    val floatingWindow: StateFlow<Boolean> = _floatingWindow.asStateFlow()

    private val _rotationDegrees = MutableStateFlow(0)
    val rotationDegrees: StateFlow<Int> = _rotationDegrees.asStateFlow()

    private val _isMirrored = MutableStateFlow(false)
    val isMirrored: StateFlow<Boolean> = _isMirrored.asStateFlow()

    private val _actionList = MutableStateFlow(ActionPresets.DEFAULT_ACTIONS)
    val actionList: StateFlow<List<FacialAction>> = _actionList.asStateFlow()

    private val _actionBeginMap = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val actionBeginMap: StateFlow<Map<Int, Long>> = _actionBeginMap.asStateFlow()

    private val _actionEndMap = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val actionEndMap: StateFlow<Map<Int, Long>> = _actionEndMap.asStateFlow()

    private val _currentActionActive = MutableStateFlow<FacialAction?>(null)
    val currentActionActive: StateFlow<FacialAction?> = _currentActionActive.asStateFlow()

    private val _actionProgressUs = MutableStateFlow(0L)
    val actionProgressUs: StateFlow<Long> = _actionProgressUs.asStateFlow()

    private var actionJob: Job? = null

    // Tri-color injection properties
    private val _colorIntensity = MutableStateFlow(prefs.autoColorIntensity)
    val colorIntensity: StateFlow<Float> = _colorIntensity.asStateFlow()

    private val _colorDiameter = MutableStateFlow(prefs.autoColorDiameter)
    val colorDiameter: StateFlow<Float> = _colorDiameter.asStateFlow()

    private val _colorX = MutableStateFlow(prefs.autoColorX)
    val colorX: StateFlow<Float> = _colorX.asStateFlow()

    private val _colorY = MutableStateFlow(prefs.autoColorY)
    val colorY: StateFlow<Float> = _colorY.asStateFlow()

    private val _monitorTargetX = MutableStateFlow(prefs.monitorTargetX)
    val monitorTargetX: StateFlow<Int> = _monitorTargetX.asStateFlow()

    private val _monitorTargetY = MutableStateFlow(prefs.monitorTargetY)
    val monitorTargetY: StateFlow<Int> = _monitorTargetY.asStateFlow()

    private val _colorMode = MutableStateFlow(
        ColorMode.entries.find { it.id == prefs.colorMode } ?: ColorMode.REALTIME_RENDER
    )
    val colorMode: StateFlow<ColorMode> = _colorMode.asStateFlow()

    private val _testMode = MutableStateFlow(1)
    val testMode: StateFlow<Int> = _testMode.asStateFlow()

    // Floating Overlay UI state
    private val _isFloatingExpanded = MutableStateFlow(true)
    val isFloatingExpanded: StateFlow<Boolean> = _isFloatingExpanded.asStateFlow()

    private val _statusNotice = MutableStateFlow<String?>(null)
    val statusNotice: StateFlow<String?> = _statusNotice.asStateFlow()

    init {
        loadActionTimings()
        refreshRootAndDaemonStatus()
        scanResidualFiles()
    }

    fun refreshRootAndDaemonStatus() {
        viewModelScope.launch {
            _injectionStatus.value = injector.getStatus()
        }
    }

    fun deployNativeEngine() {
        viewModelScope.launch {
            _deployProgress.value = "正在启动部署..."
            val success = injector.deployAndStartDaemon { progress ->
                _deployProgress.value = progress
            }
            delay(1000)
            _deployProgress.value = null
            _injectionStatus.value = injector.getStatus()
            if (success) {
                showNotice("虚拟相机底层服务已成功部署并连接")
                // Sync current settings to native
                syncStateToNative()
            } else {
                showNotice("部署未完成，请确认设备是否具有ROOT权限")
            }
        }
    }

    fun stopDaemon() {
        viewModelScope.launch {
            injector.stopDaemon()
            _injectionStatus.value = injector.getStatus()
            showNotice("已停止 vcplax 守护进程")
        }
    }

    fun scanResidualFiles() {
        viewModelScope.launch {
            _residualResult.value = injector.scanResidualFiles()
        }
    }

    fun cleanResidualFiles() {
        viewModelScope.launch {
            val success = injector.cleanResidualFiles()
            _residualResult.value = injector.scanResidualFiles()
            _injectionStatus.value = injector.getStatus()
            if (success) {
                showNotice("已清除系统残留文件 (防风控安全已重置)")
            } else {
                showNotice("清理需要ROOT权限")
            }
        }
    }

    private fun syncStateToNative() {
        val bridge = injector.bridge ?: return
        val isStream = _replacementMode.value == ReplacementMode.RTMP
        val path = if (isStream) _rtmpUrl.value else _videoPath.value
        bridge.setVideoSource(path, _isLoop.value, isStream)
        bridge.setReplaceCameraEnabled(_isReplacing.value)
        bridge.setMirrorFlip(_isMirrored.value)
        bridge.setTriColorLight(
            _colorMode.value.id,
            _colorIntensity.value,
            _colorDiameter.value,
            _colorX.value,
            _colorY.value,
            _testMode.value
        )
    }

    private fun loadActionTimings() {
        val begins = mutableMapOf<Int, Long>()
        val ends = mutableMapOf<Int, Long>()
        ActionPresets.DEFAULT_ACTIONS.forEach { action ->
            begins[action.id] = prefs.getActionBegin(action.id, action.defaultStartUs)
            ends[action.id] = prefs.getActionEnd(action.id, action.defaultEndUs)
        }
        _actionBeginMap.value = begins
        _actionEndMap.value = ends
    }

    fun setReplacementMode(mode: ReplacementMode) {
        _replacementMode.value = mode
        prefs.replacementMode = mode.name
    }

    fun setVideoPath(path: String) {
        _videoPath.value = path
        prefs.videoPath = path
        val isStream = _replacementMode.value == ReplacementMode.RTMP
        injector.bridge?.setVideoSource(path, _isLoop.value, isStream)
        showNotice("已选择视频: ${path.substringAfterLast('/')}")
    }

    fun setRtmpUrl(url: String) {
        _rtmpUrl.value = url
        prefs.rtmpUrl = url
        injector.bridge?.setVideoSource(url, _isLoop.value, true)
    }

    fun toggleReplaceCamera() {
        val newVal = !_isReplacing.value
        _isReplacing.value = newVal
        prefs.replaceCameraActive = newVal
        val res = injector.bridge?.setReplaceCameraEnabled(newVal)
        if (res != null && res >= 0) {
            showNotice(if (newVal) "虚拟相机已注入底层生效 (IPC通信正常)" else "底层虚拟相机已关闭")
        } else {
            showNotice(if (newVal) "虚拟相机替换已开启 (模拟模式)" else "虚拟相机替换已关闭")
        }
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        injector.bridge?.togglePlayPause()
        showNotice(if (_isPlaying.value) "已继续播放" else "已暂停播放")
    }

    fun toggleLoop() {
        val newVal = !_isLoop.value
        _isLoop.value = newVal
        prefs.isLoop = newVal
        injector.bridge?.setLoopPlayback(newVal)
        showNotice(if (newVal) "循环播放: 开启" else "循环播放: 关闭")
    }

    fun toggleFrontPreview() {
        val newVal = !_frontPreview.value
        _frontPreview.value = newVal
        prefs.frontPreviewActive = newVal
        showNotice(if (newVal) "已切换为前置摄像头预览" else "已切换为后置摄像头预览")
    }

    fun toggleAspectCorrect() {
        val newVal = !_aspectCorrect.value
        _aspectCorrect.value = newVal
        prefs.aspectCorrectActive = newVal
        showNotice(if (newVal) "画面纠正: 开启" else "画面纠正: 关闭")
    }

    fun toggleColorInject() {
        val newVal = !_colorInject.value
        _colorInject.value = newVal
        prefs.colorInjectActive = newVal
        showNotice(if (newVal) "三色注入: 开启" else "三色注入: 关闭")
    }

    fun toggleFloatingWindow() {
        val newVal = !_floatingWindow.value
        _floatingWindow.value = newVal
        prefs.floatingWindowActive = newVal
        updateFloatServiceState(newVal)
        showNotice(if (newVal) "悬浮控制窗口已开启" else "悬浮控制窗口已关闭")
    }

    fun rotate90() {
        _rotationDegrees.value = (_rotationDegrees.value + 90) % 360
        injector.bridge?.rotate90()
        showNotice("画面已旋转 ${_rotationDegrees.value}°")
    }

    fun toggleMirror() {
        _isMirrored.value = !_isMirrored.value
        injector.bridge?.setMirrorFlip(_isMirrored.value)
        showNotice(if (_isMirrored.value) "画面水平镜像: 开启" else "画面水平镜像: 关闭")
    }

    fun triggerFacialAction(actionId: Int) {
        val action = _actionList.value.find { it.id == actionId } ?: return
        val startUs = _actionBeginMap.value[actionId] ?: action.defaultStartUs
        val endUs = _actionEndMap.value[actionId] ?: action.defaultEndUs

        // Dispatch timing range and trigger to native cameraserver hook
        injector.bridge?.setActionTiming(startUs, endUs)
        injector.bridge?.triggerAction(actionId)

        actionJob?.cancel()
        actionJob = viewModelScope.launch {
            _currentActionActive.value = action
            _actionProgressUs.value = startUs
            val durationUs = (endUs - startUs).coerceAtLeast(600_000L)
            val steps = 20
            val intervalMs = (durationUs / 1000L) / steps
            for (i in 1..steps) {
                delay(intervalMs)
                _actionProgressUs.value = startUs + (durationUs * i / steps)
            }
            delay(200)
            _currentActionActive.value = null
        }
        showNotice("触发动作: ${action.name} (${startUs}μs - ${endUs}μs)")
    }

    fun updateActionRange(actionId: Int, beginUs: Long, endUs: Long) {
        val currentBegins = _actionBeginMap.value.toMutableMap()
        val currentEnds = _actionEndMap.value.toMutableMap()
        currentBegins[actionId] = beginUs
        currentEnds[actionId] = endUs
        _actionBeginMap.value = currentBegins
        _actionEndMap.value = currentEnds
        injector.bridge?.setActionTiming(beginUs, endUs)
    }

    fun saveSettings() {
        _actionBeginMap.value.forEach { (id, begin) ->
            prefs.setActionBegin(id, begin)
        }
        _actionEndMap.value.forEach { (id, end) ->
            prefs.setActionEnd(id, end)
        }
        prefs.autoColorIntensity = _colorIntensity.value
        prefs.autoColorDiameter = _colorDiameter.value
        prefs.autoColorX = _colorX.value
        prefs.autoColorY = _colorY.value
        prefs.monitorTargetX = _monitorTargetX.value
        prefs.monitorTargetY = _monitorTargetY.value
        prefs.colorMode = _colorMode.value.id

        injector.bridge?.setTriColorLight(
            _colorMode.value.id,
            _colorIntensity.value,
            _colorDiameter.value,
            _colorX.value,
            _colorY.value,
            _testMode.value
        )
        showNotice("配置已成功保存并同步至内核")
    }

    fun resetToDefaults() {
        prefs.resetToDefaults()
        loadActionTimings()
        _colorIntensity.value = 20f
        _colorDiameter.value = 25f
        _colorX.value = 50f
        _colorY.value = 50f
        _monitorTargetX.value = 540
        _monitorTargetY.value = 960
        _colorMode.value = ColorMode.REALTIME_RENDER
        showNotice("已恢复默认动作时间与三色配置")
    }

    fun setColorIntensity(value: Float) {
        _colorIntensity.value = value
        prefs.autoColorIntensity = value
    }

    fun setColorDiameter(value: Float) {
        _colorDiameter.value = value
        prefs.autoColorDiameter = value
    }

    fun setColorX(value: Float) {
        _colorX.value = value
        prefs.autoColorX = value
    }

    fun setColorY(value: Float) {
        _colorY.value = value
        prefs.autoColorY = value
    }

    fun setMonitorCoordinates(x: Int, y: Int) {
        _monitorTargetX.value = x
        _monitorTargetY.value = y
        prefs.monitorTargetX = x
        prefs.monitorTargetY = y
        showNotice("屏幕监测坐标设置为: ($x, $y)")
    }

    fun setColorMode(mode: ColorMode) {
        _colorMode.value = mode
        prefs.colorMode = mode.id
        showNotice("已切换三色模式: ${mode.title}")
    }

    fun setTestMode(mode: Int) {
        _testMode.value = mode
    }

    fun toggleFloatingExpanded() {
        _isFloatingExpanded.value = !_isFloatingExpanded.value
    }

    private fun showNotice(msg: String) {
        viewModelScope.launch {
            _statusNotice.value = msg
            delay(2500)
            if (_statusNotice.value == msg) {
                _statusNotice.value = null
            }
        }
    }

    private fun updateFloatServiceState(enabled: Boolean) {
        val app = getApplication<VLiveApp>()
        val intent = Intent(app, FloatService::class.java)
        try {
            if (enabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    app.startForegroundService(intent)
                } else {
                    app.startService(intent)
                }
            } else {
                app.stopService(intent)
            }
        } catch (_: Exception) {
            // Foreground service start permission or background restriction
        }
    }
}
