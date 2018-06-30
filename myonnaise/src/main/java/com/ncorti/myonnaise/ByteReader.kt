package com.ncorti.myonnaise

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * This class help you to read the byte line from Myo.
 * But be carefully to byte array size. There is no limitation of get() method,
 * so there is a possibility of overloading the byte buffer.
 *
 *
 * ByteReader is useful for handling raw data taken from bluetooth connection with Myo
 */
class ByteReader {

    /**
     * Raw byte array
     */
    /**
     * Return reference to byteData
     *
     * @return byteData reference
     */
    /**
     * Method for setting byteData into reader
     *
     * @param data Raw byteData read
     */
    var byteData: ByteArray? = null
        set(data) {
            field = data
            this.bbf = ByteBuffer.wrap(this.byteData)
            bbf!!.order(ByteOrder.LITTLE_ENDIAN)
        }
    /**
     * ByteBuffer for reading purpose
     */
    private var bbf: ByteBuffer? = null

    /**
     * Return a short from byteReader
     * ATTENTION: don't call this method before setByteData
     * ATTENTION: pay attention to byte array size
     *
     * @return Next short read
     */
    val short: Short
        get() = this.bbf!!.short

    /**
     * Return a byte from byteReader
     * ATTENTION: don't call this method before setByteData
     * ATTENTION: pay attention to byte array size
     *
     * @return Next byte read
     */
    val byte: Byte
        get() = this.bbf!!.get()

    /**
     * Return a int from byteReader
     * ATTENTION: don't call this method before setByteData
     * ATTENTION: pay attention to byte array size
     *
     * @return Next int read
     */
    val int: Int
        get() = this.bbf!!.int

    /**
     * Rewind byte reader to begin, for restart reading
     */
    fun rewind() {
        this.bbf!!.rewind()
    }

    /**
     * Method for reading n consecutive floats, returned in a new array
     *
     * @param size Number of bytes to be read (usually 8 or 16)
     * @return A new array with read bytes
     */
    fun getBytes(size: Int): FloatArray {
        val result = FloatArray(size)
        for (i in 0 until size)
            result[i] = bbf!!.get().toFloat()
        return result
    }
}
