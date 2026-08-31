package com.example.root

import android.os.IBinder
import android.os.Parcel
import android.util.Log

class NativeBinderBridge(private val serverName: String) {
    private var remoteBinder: IBinder? = null
    private var isConnected: Boolean = false

    private val deathRecipient = IBinder.DeathRecipient {
        Log.w(TAG, "Native Binder Service died: $serverName")
        isConnected = false
        remoteBinder = null
    }

    fun connect(): Boolean {
        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, serverName) as? IBinder

            if (binder != null && binder.isBinderAlive) {
                remoteBinder = binder
                isConnected = true
                binder.linkToDeath(deathRecipient, 0)
                Log.i(TAG, "Successfully connected to native service: $serverName")
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to native service: ${e.message}")
        }
        isConnected = false
        remoteBinder = null
        return false
    }

    fun isAlive(): Boolean {
        val binder = remoteBinder
        return binder != null && binder.isBinderAlive && isConnected
    }

    fun setVideoSource(path: String, loop: Boolean, isStream: Boolean): Int {
        return transact(TRANSACTION_SET_VIDEO_SOURCE) { data ->
            data.writeString(path)
            data.writeInt(if (loop) 1 else 0)
            data.writeInt(if (isStream) 1 else 0)
        }
    }

    fun togglePlayPause(): Int {
        return transact(TRANSACTION_TOGGLE_PLAY_PAUSE)
    }

    fun getPlaybackProgress(): IntArray {
        val binder = remoteBinder ?: return intArrayOf(0, 0)
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(DESCRIPTOR)
            binder.transact(TRANSACTION_GET_PROGRESS, data, reply, 0)
            reply.readException()
            reply.createIntArray() ?: intArrayOf(0, 0)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get progress: ${e.message}")
            intArrayOf(0, 0)
        } finally {
            reply.recycle()
            data.recycle()
        }
    }

    fun switchVideo(path: String, mode: Int): Int {
        return transact(TRANSACTION_SWITCH_VIDEO) { data ->
            data.writeInt(mode)
            data.writeString(path)
        }
    }

    fun rotate90(): Int {
        return transact(TRANSACTION_ROTATE_90)
    }

    fun setMirrorFlip(mirror: Boolean): Int {
        return transact(TRANSACTION_SET_MIRROR) { data ->
            data.writeInt(if (mirror) 1 else 0)
        }
    }

    fun setReplaceCameraEnabled(enable: Boolean): Int {
        return transact(TRANSACTION_SET_REPLACE_CAMERA) { data ->
            data.writeInt(if (enable) 1 else 0)
        }
    }

    fun triggerAction(actionId: Int): Int {
        return transact(TRANSACTION_TRIGGER_ACTION) { data ->
            data.writeInt(actionId)
        }
    }

    fun setLoopPlayback(loop: Boolean): Int {
        return transact(TRANSACTION_SET_LOOP) { data ->
            data.writeInt(if (loop) 1 else 0)
        }
    }

    fun setActionTiming(startUs: Long, endUs: Long): Int {
        return transact(TRANSACTION_SET_ACTION_TIMING) { data ->
            data.writeLong(startUs)
            data.writeLong(endUs)
        }
    }

    fun setTriColorLight(
        mode: Int,
        intensity: Float,
        diameter: Float,
        x: Float,
        y: Float,
        preset: Int
    ): Int {
        return transact(TRANSACTION_SET_TRI_COLOR_LIGHT) { data ->
            data.writeInt(mode)
            data.writeFloat(intensity)
            data.writeFloat(diameter)
            data.writeFloat(x)
            data.writeFloat(y)
            data.writeInt(preset)
        }
    }

    fun resetState(): Int {
        return transact(TRANSACTION_RESET_STATE)
    }

    private inline fun transact(code: Int, writeArgs: (Parcel) -> Unit = {}): Int {
        val binder = remoteBinder ?: return -1
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(DESCRIPTOR)
            writeArgs(data)
            binder.transact(code, data, reply, 0)
            reply.readException()
            reply.readInt()
        } catch (e: Exception) {
            Log.e(TAG, "Transaction $code failed: ${e.message}")
            -1
        } finally {
            reply.recycle()
            data.recycle()
        }
    }

    companion object {
        private const val TAG = "NativeBinderBridge"
        private const val DESCRIPTOR = "com.xiaomi.vlive.IMyBinderService"

        const val TRANSACTION_SET_VIDEO_SOURCE = 11
        const val TRANSACTION_TOGGLE_PLAY_PAUSE = 12
        const val TRANSACTION_GET_PROGRESS = 13
        const val TRANSACTION_SWITCH_VIDEO = 14
        const val TRANSACTION_ROTATE_90 = 15
        const val TRANSACTION_SET_MIRROR = 16
        const val TRANSACTION_SET_REPLACE_CAMERA = 17
        const val TRANSACTION_TRIGGER_ACTION = 18
        const val TRANSACTION_SET_LOOP = 19
        const val TRANSACTION_SET_ACTION_TIMING = 22
        const val TRANSACTION_SET_TRI_COLOR_LIGHT = 24
        const val TRANSACTION_RESET_STATE = 25
    }
}
