package com.ncorti.myonnaise

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandListTest {

    @Test
    fun stopStreaming_isProperlyPopulated() {
        val command = CommandList.stopStreaming()

        assertEquals(0x01.toByte(), command[0])
        assertEquals(3.toByte(), command[1])
        assertEquals(0x00.toByte(), command[2])
        assertEquals(0x00.toByte(), command[3])
        assertEquals(0x00.toByte(), command[4])
    }

    @Test
    fun isStartStreamingCommand_withEmgFilteredOnly() {
        val command = CommandList.emgFilteredOnly()

        assertTrue(command.isStartStreamingCommand())
    }

    @Test
    fun isStartStreamingCommand_withEmgUnfilteredOnly() {
        val command = CommandList.emgUnfilteredOnly()

        assertTrue(command.isStartStreamingCommand())
    }

    @Test
    fun isStartStreamingCommand_withStopStreamingCommand() {
        val command = CommandList.stopStreaming()

        assertFalse(command.isStartStreamingCommand())
    }

    @Test
    fun isStopStreamingCommand_withStopStreamingCommand() {
        val command = CommandList.stopStreaming()

        assertTrue(command.isStopStreamingCommand())
    }

    @Test
    fun isStopStreamingCommand_withStartStreamingCommand() {
        val command = CommandList.emgUnfilteredOnly()

        assertFalse(command.isStopStreamingCommand())
    }
}
