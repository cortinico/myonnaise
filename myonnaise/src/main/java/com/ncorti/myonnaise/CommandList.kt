@file:Suppress("LocalVariableName")

package com.ncorti.myonnaise

import java.util.*

typealias Command = ByteArray

/**
 * This class is List of Myo Commands, allowing to
 * [https://github.com/thalmiclabs/myo-bluetooth].
 */
object CommandList {

    fun stopStreaming(): Command {
        val command_data = 0x01.toByte()
        val payload_data = 3.toByte()
        val emg_mode = 0x00.toByte()
        val imu_mode = 0x00.toByte()
        val class_mode = 0x00.toByte()
        return byteArrayOf(command_data, payload_data, emg_mode, imu_mode, class_mode)
    }

    fun emgFilteredOnly(): Command {
        val command_data = 0x01.toByte()
        val payload_data = 3.toByte()
        val emg_mode = 0x02.toByte()
        val imu_mode = 0x00.toByte()
        val class_mode = 0x00.toByte()
        return byteArrayOf(command_data, payload_data, emg_mode, imu_mode, class_mode)
    }

    fun emgUnfilteredOnly(): Command {
        val command_data = 0x01.toByte()
        val payload_data = 3.toByte()
        val emg_mode = 0x03.toByte()
        val imu_mode = 0x00.toByte()
        val class_mode = 0x00.toByte()
        return byteArrayOf(command_data, payload_data, emg_mode, imu_mode, class_mode)
    }

    fun vibration1(): Command {
        val command_vibrate = 0x03.toByte()
        val payload_vibrate = 1.toByte()
        val vibrate_type = 0x01.toByte()
        return byteArrayOf(command_vibrate, payload_vibrate, vibrate_type)
    }

    fun vibration2(): Command {
        val command_vibrate = 0x03.toByte()
        val payload_vibrate = 1.toByte()
        val vibrate_type = 0x02.toByte()
        return byteArrayOf(command_vibrate, payload_vibrate, vibrate_type)
    }

    fun vibration3(): Command {
        val command_vibrate = 0x03.toByte()
        val payload_vibrate = 1.toByte()
        val vibrate_type = 0x03.toByte()
        return byteArrayOf(command_vibrate, payload_vibrate, vibrate_type)
    }

    fun unSleep(): Command {
        val command_sleep_mode = 0x09.toByte()
        val payload_unlock = 1.toByte()
        val never_sleep = 1.toByte() // Never go to sleep.
        return byteArrayOf(command_sleep_mode, payload_unlock, never_sleep)
    }

    fun normalSleep(): Command {
        val command_sleep_mode = 0x09.toByte()
        val payload_unlock = 1.toByte()
        val normal_sleep = 0.toByte() // Normal sleep mode; Myo will sleep after a period of inactivity.
        return byteArrayOf(command_sleep_mode, payload_unlock, normal_sleep)
    }
}

fun Command.isStartStreamingCommand() = this.size >= 4
        && this[0] == 0x01.toByte()
        && (this[2] != 0x00.toByte() || this[3] != 0x00.toByte() || this[4] != 0x00.toByte())

fun Command.isStopStreamingCommand() = Arrays.equals(this, CommandList.stopStreaming())
