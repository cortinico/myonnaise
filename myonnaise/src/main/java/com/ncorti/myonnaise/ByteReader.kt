package com.ncorti.myonnaise

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * This class help you to read the byte line from Myo.
 * But be carefully to byte array size. There is no limitation of get() method,
 * so there is a possibility of overloading the byte buffer.
 *
 * [ByteReader] is useful for handling raw data taken from bluetooth connection with Myo
 */
class ByteReader {

    internal var byteBuffer: ByteBuffer? = null

    var byteData: ByteArray? = null
        set(data) {
            field = data
            this.byteBuffer = ByteBuffer.wrap(field)
            byteBuffer?.order(ByteOrder.nativeOrder())
        }

    val short: Short
        get() = this.byteBuffer!!.short

    val byte: Byte
        get() = this.byteBuffer!!.get()

    val int: Int
        get() = this.byteBuffer!!.int

    fun rewind() = this.byteBuffer?.rewind()

    /**
     * Method for reading n consecutive floats, returned in a new array
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
