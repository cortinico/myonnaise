package com.ncorti.myonnaise

@Suppress("LocalVariableName", "PrivatePropertyName")
/**
 * This class is List of Myo Commands, allowing to
 * [https://github.com/thalmiclabs/myo-bluetooth].
 */
class MyoCommandList {

    private var send_bytes_data: ByteArray? = null

    fun sendUnsetData(): ByteArray? {
        val command_data = 0x01.toByte()
        val payload_data = 3.toByte()
        val emg_mode = 0x00.toByte()
        val imu_mode = 0x00.toByte()
        val class_mode = 0x00.toByte()
        send_bytes_data = byteArrayOf(command_data, payload_data, emg_mode, imu_mode, class_mode)

        return send_bytes_data
    }

    fun sendVibration3(): ByteArray? {
        val command_vibrate = 0x03.toByte()
        val payload_vibrate = 1.toByte()
        val vibrate_type = 0x03.toByte()
        send_bytes_data = byteArrayOf(command_vibrate, payload_vibrate, vibrate_type)

        return send_bytes_data
    }

    fun sendEmgOnly(): ByteArray? {
        val command_data = 0x01.toByte()
        val payload_data = 3.toByte()
        val emg_mode = 0x02.toByte()
        val imu_mode = 0x00.toByte()
        val class_mode = 0x00.toByte()
        send_bytes_data = byteArrayOf(command_data, payload_data, emg_mode, imu_mode, class_mode)

        return send_bytes_data
    }

    fun sendUnSleep(): ByteArray? {
        val command_sleep_mode = 0x09.toByte()
        val payload_unlock = 1.toByte()
        val never_sleep = 1.toByte()
        send_bytes_data = byteArrayOf(command_sleep_mode, payload_unlock, never_sleep)

        return send_bytes_data
    }

    fun sendNormalSleep(): ByteArray? {
        val command_sleep_mode = 0x09.toByte()
        val payload_unlock = 1.toByte()
        val normal_sleep = 0.toByte()
        send_bytes_data = byteArrayOf(command_sleep_mode, payload_unlock, normal_sleep)

        return send_bytes_data
    }
}
