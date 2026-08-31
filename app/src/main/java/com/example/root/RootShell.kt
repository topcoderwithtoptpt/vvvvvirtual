package com.example.root

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

object RootShell {
    private const val TAG = "RootShell"
    private var suProcess: Process? = null
    private var outputStream: DataOutputStream? = null

    @Synchronized
    private fun ensureShell(): Boolean {
        if (suProcess != null && outputStream != null) {
            return true
        }
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            suProcess = process
            outputStream = os
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize root shell: ${e.message}")
            suProcess = null
            outputStream = null
            false
        }
    }

    suspend fun execute(command: String): String = withContext(Dispatchers.IO) {
        synchronized(this) {
            try {
                if (!ensureShell()) {
                    return@withContext "ERROR: su unavailable"
                }

                val os = outputStream ?: return@withContext "ERROR: output stream null"
                val process = suProcess ?: return@withContext "ERROR: process null"

                val marker = "EOF_MARK_${System.currentTimeMillis()}"
                val cmdToSend = "$command\necho $marker\n"
                os.writeBytes(cmdToSend)
                os.flush()

                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val resultBuilder = StringBuilder()
                var line: String?

                while (true) {
                    line = reader.readLine()
                    if (line == null) break
                    if (line.trim() == marker) break
                    resultBuilder.append(line).append("\n")
                }

                resultBuilder.toString().trim()
            } catch (e: Exception) {
                Log.e(TAG, "Root execution error: ${e.message}")
                suProcess = null
                outputStream = null
                "ERROR: ${e.message}"
            }
        }
    }

    suspend fun isRootAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val output = execute("id")
            output.contains("uid=0")
        } catch (_: Exception) {
            false
        }
    }

    suspend fun setSELinuxPermissive(): Boolean = withContext(Dispatchers.IO) {
        try {
            execute("setenforce 0")
            val mode = execute("getenforce")
            mode.equals("Permissive", ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getSELinuxMode(): String = withContext(Dispatchers.IO) {
        try {
            val mode = execute("getenforce")
            if (mode.startsWith("ERROR")) "Unknown" else mode.trim()
        } catch (_: Exception) {
            "Unknown"
        }
    }

    suspend fun isProcessRunning(processName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val output = execute("pgrep -f $processName || ps -ef | grep $processName | grep -v grep")
            output.isNotBlank() && !output.startsWith("ERROR")
        } catch (_: Exception) {
            false
        }
    }

    suspend fun killProcess(processName: String) = withContext(Dispatchers.IO) {
        execute("killall $processName")
        execute("pkill -9 -f $processName")
    }

    suspend fun detectArchitecture(): String = withContext(Dispatchers.IO) {
        try {
            val fileOut = execute("file /system/bin/cameraserver")
            if (fileOut.contains("64-bit") || fileOut.contains("aarch64") || fileOut.contains("arm64")) {
                "arm64-v8a"
            } else if (fileOut.contains("32-bit") || fileOut.contains("ARM")) {
                "armeabi-v7a"
            } else {
                val abiOut = execute("getprop ro.product.cpu.abi")
                if (abiOut.contains("arm64")) "arm64-v8a" else "armeabi-v7a"
            }
        } catch (_: Exception) {
            "arm64-v8a"
        }
    }
}
