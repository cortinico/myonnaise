package com.ncorti.myonnaise

import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ByteReaderTest {

    @Test
    fun getByte() {
        val testReader = ByteReader()
        testReader.byteData = byteArrayOf(1.toByte())

        assertEquals(1.toByte(), testReader.byte)
    }

    @Test
    fun getInt() {
        val intArray = ByteBuffer
                .allocate(4)
                .order(ByteOrder.nativeOrder())
                .putInt(12345)
                .array()

        val testReader = ByteReader()
        testReader.byteData = intArray

        assertEquals(12345, testReader.int)
    }


    @Test
    fun getShort() {
        val shortArray = ByteBuffer
                .allocate(2)
                .order(ByteOrder.nativeOrder())
                .putShort(123.toShort())
                .array()

        val testReader = ByteReader()
        testReader.byteData = shortArray

        assertEquals(123.toShort(), testReader.short)
    }

    @Test
    fun rewindWorks() {
        val testReader = ByteReader()
        testReader.byteData = byteArrayOf(1.toByte())

        assertEquals(1.toByte(), testReader.byte)
        testReader.rewind()
        assertEquals(1.toByte(), testReader.byte)
    }

    @Test
    fun getBytes() {
        val testReader = ByteReader()
        testReader.byteData = byteArrayOf(
                0.toByte(),
                1.toByte(),
                2.toByte(),
                3.toByte(),
                4.toByte(),
                5.toByte(),
                6.toByte(),
                7.toByte()
        )

        val resultArray = testReader.getBytes(8)
        assertEquals(8, resultArray.size)

        for (i in 0 until resultArray.size) {
            assertEquals(i.toFloat(), resultArray[i])
        }
    }
}