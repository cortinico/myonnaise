package com.ncorti.myonnaise

import android.bluetooth.BluetoothGattCharacteristic
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * This class help you to read the byte line from Myo.
 * Please pay attention that there are no checks for BufferOverFlow or similar problems,
 * you just receive the amount of data you request (1, 2 or 4 bytes).
 *
 * [ByteReader] is useful for handling raw data taken from bluetooth connection with Myo.
 * Use the [ByteReader.getBytes] to get an array of float from a [BluetoothGattCharacteristic].
 */
class ByteReader {

    private var byteBuffer: ByteBuffer? = null

    var byteData: ByteArray? = null
        set(data) {
            field = data
            this.byteBuffer = field?.let { ByteBuffer.wrap(it) }?.apply {
                order(ByteOrder.nativeOrder())
            }
        }

    val short: Short
        get() = this.byteBuffer!!.short

    val byte: Byte
        get() = this.byteBuffer!!.get()

    val int: Int
        get() = this.byteBuffer!!.int

    fun rewind() = this.byteBuffer?.rewind()

    /**
     * Method for reading n consecutive floats, returned in a new array.
     *
     * @param size Number of bytes to be read (usually 8 or 16)
     * @return A new array with read bytes
     */
    fun getBytes(size: Int): FloatArray {
        val result = FloatArray(size)
        for (i in 0 until size)
            result[i] = byteBuffer!!.get().toFloat()
        return result
    }
}
