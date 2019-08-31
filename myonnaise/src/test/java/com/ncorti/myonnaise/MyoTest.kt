package com.ncorti.myonnaise

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MyoTest {

    private lateinit var mockContext: Context
    private lateinit var mockBlDevice: BluetoothDevice

    @Before
    fun setup() {
        mockBlDevice = mock {
            on(mock.name) doReturn "42"
            on(mock.address) doReturn "11:22:33:44:55:66"
        }
        mockContext = mock {}
    }

    @Test
    fun getName() {
        assertEquals("42", Myo(mockBlDevice).name)
    }

    @Test
    fun getAddress() {
        assertEquals("42", Myo(mockBlDevice).name)
    }

    @Test
    fun getDefaultFrequency() {
        assertEquals(0, Myo(mockBlDevice).frequency)
    }

    @Test
    fun updatingFrequencyToNonDefault() {
        val myo = Myo(mockBlDevice)
        myo.frequency = 50
        assertEquals(50, myo.frequency)
    }

    @Test
    fun updatingFrequencyToNonSupported_ResetToDefault() {
        val myo = Myo(mockBlDevice)
        myo.frequency = MYO_MAX_FREQUENCY + 1
        assertEquals(0, myo.frequency)
    }

    @Test
    fun connect_callGattConnection() {
        val mockContext = mock<Context> {}
        val myo = Myo(mockBlDevice)
        myo.connect(mockContext)

        verify(mockBlDevice).connectGatt(eq(mockContext), eq(false), eq(myo))
    }

    @Test
    fun connect_publishConnectingEvent() {
        val mockContext = mock<Context> {}
        val myo = Myo(mockBlDevice)

        myo.connect(mockContext)
        myo.statusObservable().test().assertValue(MyoStatus.CONNECTING)
    }

    @Test
    fun disconnect_closeGatt() {
        val myo = Myo(mockBlDevice)
        myo.gatt = mock {}
        myo.disconnect()

        verify(myo.gatt)?.close()
    }

    @Test
    fun disconnect_publishEvents() {
        val myo = Myo(mockBlDevice)
        myo.gatt = mock {}
        myo.disconnect()

        myo.statusObservable().test().assertValue(MyoStatus.DISCONNECTED)
        myo.controlObservable().test().assertValue(MyoControlStatus.NOT_STREAMING)
    }

    @Test
    fun isConnected_whenActuallyConnected() {
        val myo = Myo(mockBlDevice)
        myo.connectionStatusSubject.onNext(MyoStatus.CONNECTED)
        assertTrue(myo.isConnected())

        myo.connectionStatusSubject.onNext(MyoStatus.READY)
        assertTrue(myo.isConnected())
    }

    @Test
    fun isConnected_whenNotConnected() {
        val myo = Myo(mockBlDevice)
        myo.connectionStatusSubject.onNext(MyoStatus.CONNECTING)
        assertFalse(myo.isConnected())

        myo.connectionStatusSubject.onNext(MyoStatus.DISCONNECTED)
        assertFalse(myo.isConnected())
    }

    @Test
    fun isStreaming_whenActuallyStreaming() {
        val myo = Myo(mockBlDevice)
        myo.controlStatusSubject.onNext(MyoControlStatus.STREAMING)
        assertTrue(myo.isStreaming())
    }

    @Test
    fun isStreaming_whenNotStreaming() {
        val myo = Myo(mockBlDevice)
        myo.controlStatusSubject.onNext(MyoControlStatus.NOT_STREAMING)
        assertFalse(myo.isStreaming())
    }

    @Test
    fun sendCommand_withCharacteristicNotReady() {
        val myo = Myo(mockBlDevice)
        myo.characteristicCommand = null

        assertFalse(myo.sendCommand(CommandList.vibration1()))
    }

    @Test
    fun sendCommand_withCharacteristicReady_writeTheCommand() {
        val myo = Myo(mockBlDevice)
        myo.gatt = mock { }
        myo.characteristicCommand = mock {
            on(mock.properties) doReturn BluetoothGattCharacteristic.PROPERTY_WRITE
        }

        assertTrue(myo.sendCommand(CommandList.vibration1()))
        verify(myo.gatt)?.writeCharacteristic(myo.characteristicCommand)
    }

    @Test
    fun sendCommand_withStartStreamingCommand_publishEvent() {
        val myo = Myo(mockBlDevice)
        myo.gatt = mock { }
        myo.characteristicCommand = mock {
            on(mock.properties) doReturn BluetoothGattCharacteristic.PROPERTY_WRITE
        }

        myo.sendCommand(CommandList.emgFilteredOnly())
        myo.controlObservable().test().assertValue(MyoControlStatus.STREAMING)
    }

    @Test
    fun sendCommand_withStopStreamingCommand_publishEvent() {
        val myo = Myo(mockBlDevice)
        myo.gatt = mock { }
        myo.characteristicCommand = mock {
            on(mock.properties) doReturn BluetoothGattCharacteristic.PROPERTY_WRITE
        }

        myo.sendCommand(CommandList.stopStreaming())
        myo.controlObservable().test().assertValue(MyoControlStatus.NOT_STREAMING)
    }

    @Test
    fun writeDescriptor_withEmptyQueue_writeImmediately() {
        val mockDescriptor = mock<BluetoothGattDescriptor> {}
        val myo = Myo(mockBlDevice)
        val mockGatt = mock<BluetoothGatt> {}
        myo.writeQueue.clear()

        myo.writeDescriptor(mockGatt, mockDescriptor)

        verify(mockGatt).writeDescriptor(any())
    }

    @Test
    fun writeDescriptor_withNotEmptyQueue_postponeWrite() {
        val mockDescriptor = mock<BluetoothGattDescriptor> {}
        val myo = Myo(mockBlDevice)
        myo.gatt = mock {}
        myo.writeQueue.add(mockDescriptor)

        myo.writeDescriptor(mock {}, mockDescriptor)

        verify(myo.gatt, never())?.writeDescriptor(mockDescriptor)
    }
}