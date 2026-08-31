package com.example.root

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipFile

data class InjectionStatus(
    val isRootGranted: Boolean = false,
    val isDaemonRunning: Boolean = false,
    val isBinderConnected: Boolean = false,
    val seLinuxMode: String = "Unknown",
    val architecture: String = "arm64-v8a",
    val serverName: String = "",
    val daemonPid: String = "",
    val message: String = "未初始化"
)

data class ResidualScanResult(
    val detectedPaths: List<String> = emptyList(),
    val isClean: Boolean = true
)

class NativeCameraInjector(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_config", Context.MODE_PRIVATE)

    var bridge: NativeBinderBridge? = null
        private set

    fun getServerName(): String {
        var serverName = prefs.getString("ServerName", "") ?: ""
        if (serverName.isBlank()) {
            serverName = "vlive_" + UUID.randomUUID().toString().replace("-", "").take(10)
            prefs.edit().putString("ServerName", serverName).apply()
        }
        return serverName
    }

    suspend fun getStatus(): InjectionStatus = withContext(Dispatchers.IO) {
        val isRoot = RootShell.isRootAvailable()
        val seLinux = if (isRoot) RootShell.getSELinuxMode() else "Unknown"
        val arch = if (isRoot) RootShell.detectArchitecture() else "arm64-v8a"
        val isRunning = if (isRoot) RootShell.isProcessRunning("vcplax") else false
        val sName = getServerName()

        val binderAlive = bridge?.isAlive() ?: false

        var pid = ""
        if (isRunning) {
            val pidOut = RootShell.execute("pidof vcplax || pgrep -f vcplax")
            pid = pidOut.lines().firstOrNull()?.trim() ?: ""
        }

        InjectionStatus(
            isRootGranted = isRoot,
            isDaemonRunning = isRunning,
            isBinderConnected = binderAlive,
            seLinuxMode = seLinux,
            architecture = arch,
            serverName = sName,
            daemonPid = pid,
            message = when {
                !isRoot -> "未获取ROOT权限 (当前为界面预览模式)"
                !isRunning -> "ROOT已授权，但底层守护进程 vcplax 未启动"
                !binderAlive -> "守护进程已启动(PID $pid)，正在连接Binder服务..."
                else -> "底层虚拟相机已完全就绪 (PID: $pid, 服务: $sName)"
            }
        )
    }

    suspend fun deployAndStartDaemon(onProgress: (String) -> Unit = {}): Boolean = withContext(Dispatchers.IO) {
        try {
            onProgress("正在检查 ROOT 权限...")
            val isRoot = RootShell.isRootAvailable()
            if (!isRoot) {
                onProgress("失败: 设备未获取 ROOT 权限")
                return@withContext false
            }

            onProgress("设置 SELinux 为 Permissive (宽容模式)...")
            RootShell.setSELinuxPermissive()

            onProgress("识别 cameraserver 系统架构...")
            val arch = RootShell.detectArchitecture()
            Log.i(TAG, "Detected architecture: $arch")

            onProgress("解压底层核心库 ($arch)...")
            val targetDir = File(context.filesDir, "libs/$arch")
            targetDir.mkdirs()

            val libs = listOf("libshadowhook.so", "libvc.so", "vcplax.so")
            for (lib in libs) {
                extractLibFile(arch, lib, targetDir)
            }

            onProgress("终止旧版 vcplax 进程...")
            RootShell.killProcess("vcplax")
            delay(300)

            onProgress("部署核心动态库至 /data/ ...")
            val libvcFile = File(targetDir, "libvc.so").absolutePath
            val libShadowFile = File(targetDir, "libshadowhook.so").absolutePath
            val vcplaxFile = File(targetDir, "vcplax.so").absolutePath

            RootShell.execute("cp -f $libvcFile /data/libvc.so")
            RootShell.execute("cp -f $libShadowFile /data/libvc++.so")
            RootShell.execute("cp -f $vcplaxFile /data/vcplax")
            RootShell.execute("chmod 700 /data/vcplax")
            RootShell.execute("chmod 755 /data/libvc.so")
            RootShell.execute("chmod 755 /data/libvc++.so")

            val serverName = getServerName()
            onProgress("启动底层守护进程: /data/vcplax $serverName ...")
            RootShell.execute("/data/vcplax $serverName &")

            delay(800)

            onProgress("连接 Binder IPC 通信服务 ($serverName)...")
            val newBridge = NativeBinderBridge(serverName)
            var connected = false
            for (retry in 1..5) {
                if (newBridge.connect()) {
                    connected = true
                    break
                }
                delay(400)
            }

            bridge = newBridge

            if (connected) {
                onProgress("虚拟相机底层注入服务启动成功！")
                true
            } else {
                val running = RootShell.isProcessRunning("vcplax")
                if (running) {
                    onProgress("守护进程已在后台运行，正在等待ServiceManager注册...")
                    true
                } else {
                    onProgress("启动失败: vcplax 未能正常运行")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Deploy error: ${e.message}", e)
            onProgress("部署异常: ${e.message}")
            false
        }
    }

    private fun extractLibFile(arch: String, libName: String, targetDir: File) {
        val destFile = File(targetDir, libName)
        if (destFile.exists() && destFile.length() > 0) {
            return
        }

        // Try 1: extract from assets/libs/<arch>/<libName>
        try {
            val assetPath = "libs/$arch/$libName"
            context.assets.open(assetPath).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            if (destFile.exists() && destFile.length() > 0) {
                return
            }
        } catch (_: Exception) {
            // Fallback to ZipFile
        }

        // Try 2: extract from APK sourceDir
        try {
            val apkPath = context.applicationInfo.sourceDir
            ZipFile(apkPath).use { zip ->
                val entry = zip.getEntry("lib/$arch/$libName")
                    ?: zip.getEntry("assets/libs/$arch/$libName")
                if (entry != null) {
                    zip.getInputStream(entry).use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract $libName: ${e.message}")
        }
    }

    suspend fun scanResidualFiles(): ResidualScanResult = withContext(Dispatchers.IO) {
        val suspectPaths = listOf(
            "/data/camera/libshadowhook.so",
            "/data/samera/libshadowhook.so",
            "/data/vcplax",
            "/data/libvc.so",
            "/data/libvc++.so"
        )
        val detected = mutableListOf<String>()
        if (RootShell.isRootAvailable()) {
            for (path in suspectPaths) {
                val out = RootShell.execute("[ -f $path ] && echo 1 || echo 0")
                if (out.trim() == "1") {
                    detected.add(path)
                }
            }
        }
        ResidualScanResult(
            detectedPaths = detected,
            isClean = detected.isEmpty()
        )
    }

    suspend fun cleanResidualFiles(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!RootShell.isRootAvailable()) return@withContext false
            RootShell.killProcess("vcplax")
            val paths = listOf(
                "/data/camera/libshadowhook.so",
                "/data/samera/libshadowhook.so",
                "/data/vcplax",
                "/data/libvc.so",
                "/data/libvc++.so"
            )
            for (path in paths) {
                RootShell.execute("rm -f $path")
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun stopDaemon(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (RootShell.isRootAvailable()) {
                RootShell.killProcess("vcplax")
                bridge = null
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        private const val TAG = "NativeCameraInjector"
    }
}
