@file:Suppress("MagicNumber")

package com.ncorti.myonnaise

import java.util.Arrays

typealias Command = ByteArray

/**
 * List of commands you can send to a [Myo] via the [Myo.sendCommand] method.
 * A [Command] is basically a [ByteArray] with all the bytes properly set.
 *
 * This is defined according to the Myo's Bluetooth specs defined here:
 * https://github.com/thalmiclabs/myo-bluetooth
 */
object CommandList {

    /** Stop all the Streaming from the device (EMG, IMU and Classifier) */
    fun stopStreaming(): Command {
        val command_data = 0x01.toByte()
        val payload_data = 3.toByte()
        val emg_mode = 0x00.toByte()
        val imu_mode = 0x00.toByte()
        val class_mode = 0x00.toByte()
        return byteArrayOf(command_data, payload_data, emg_mode, imu_mode, class_mode)
    }

    /** Start the EMG Streaming (filtered) */
    fun emgFilteredOnly(): Command {
        val command_data = 0x01.toByte()
        val payload_data = 3.toByte()
        val emg_mode = 0x02.toByte()
        val imu_mode = 0x00.toByte()
        val class_mode = 0x00.toByte()
        return byteArrayOf(command_data, payload_data, emg_mode, imu_mode, class_mode)
    }

    /** Start the EMG Streaming (unfiltered) */
    fun emgUnfilteredOnly(): Command {
        val command_data = 0x01.toByte()
        val payload_data = 3.toByte()
        val emg_mode = 0x03.toByte()
        val imu_mode = 0x00.toByte()
        val class_mode = 0x00.toByte()
        return byteArrayOf(command_data, payload_data, emg_mode, imu_mode, class_mode)
    }

    /** Send a short vibration */
    fun vibration1(): Command {
        val command_vibrate = 0x03.toByte()
        val payload_vibrate = 1.toByte()
        val vibrate_type = 0x01.toByte()
        return byteArrayOf(command_vibrate, payload_vibrate, vibrate_type)
    }

    /** Send a medium vibration */
    fun vibration2(): Command {
        val command_vibrate = 0x03.toByte()
        val payload_vibrate = 1.toByte()
        val vibrate_type = 0x02.toByte()
        return byteArrayOf(command_vibrate, payload_vibrate, vibrate_type)
    }

    /** Send a long vibration */
    fun vibration3(): Command {
        val command_vibrate = 0x03.toByte()
        val payload_vibrate = 1.toByte()
        val vibrate_type = 0x03.toByte()
        return byteArrayOf(command_vibrate, payload_vibrate, vibrate_type)
    }

    /** Send an unsleep command. Needed to keep the Myo awake */
    fun unSleep(): Command {
        val command_sleep_mode = 0x09.toByte()
        val payload_unlock = 1.toByte()
        val never_sleep = 1.toByte() // Never go to sleep.
        return byteArrayOf(command_sleep_mode, payload_unlock, never_sleep)
    }

    /** Send a normal sleep command. The myo will sleep if no interaction are recorded */
    fun normalSleep(): Command {
        val command_sleep_mode = 0x09.toByte()
        val payload_unlock = 1.toByte()
        val normal_sleep = 0.toByte() // Normal sleep mode; Myo will sleep after a period of inactivity.
        return byteArrayOf(command_sleep_mode, payload_unlock, normal_sleep)
    }
}

/** Extension function to check the [Command] is a generic "start streaming" command. */
fun Command.isStartStreamingCommand() =
    this.size >= 4 &&
        this[0] == 0x01.toByte() &&
        (this[2] != 0x00.toByte() || this[3] != 0x00.toByte() || this[4] != 0x00.toByte())

/** Extension function to check the [Command] is a stop streaming command */
fun Command.isStopStreamingCommand() = Arrays.equals(this, CommandList.stopStreaming())
