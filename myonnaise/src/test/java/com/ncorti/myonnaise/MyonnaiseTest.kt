package com.ncorti.myonnaise

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.content.Context
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.subscribers.TestSubscriber
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MyonnaiseTest {

    lateinit var mockContext: Context
    lateinit var mockBlManager: BluetoothManager
    lateinit var mockBlAdapter: BluetoothAdapter
    lateinit var mockBlLeScanner: BluetoothLeScanner
    lateinit var mockBlDevice: BluetoothDevice

    @Before
    fun setup() {
        mockBlDevice = mock {
            on(mock.name) doReturn "42"
            on(mock.address) doReturn "11:22:33:44:55:66"
        }
        mockBlLeScanner = mock {}
        mockBlAdapter = mock {
            on(mock.bluetoothLeScanner) doReturn mockBlLeScanner
        }
        mockBlManager = mock {
            on(mock.adapter) doReturn mockBlAdapter
        }
        mockContext = mock {
            on(mock.getSystemService(Activity.BLUETOOTH_SERVICE)) doReturn mockBlManager
        }
    }

    @Test
    fun startScan_triggerLeScan() {
        val testSubscriber = TestSubscriber<BluetoothDevice>()
        Myonnaise(mockContext).startScan().subscribe(testSubscriber)

        verify(mockBlLeScanner).startScan(any())
    }

    @Test
    fun startScan_whenCancelling_stopLeScan() {
        val testSubscriber = TestSubscriber<BluetoothDevice>()
        Myonnaise(mockContext).startScan().subscribe(testSubscriber)

        testSubscriber.cancel()
        verify(mockBlLeScanner).stopScan(any<ScanCallback>())
    }

    @Test
    fun startScan_withTimeout_stopLeScan() {
        val testSubscriber = TestSubscriber<BluetoothDevice>()
        Myonnaise(mockContext).startScan(100, TimeUnit.MILLISECONDS).subscribe(testSubscriber)

        testSubscriber.await(200, TimeUnit.MILLISECONDS)
        verify(mockBlLeScanner).stopScan(any<ScanCallback>())
    }

    @Test
    fun getMyoFromBluetoothDevice() {
        val myo = Myonnaise(mockContext).getMyo(mockBlDevice)
        assertEquals("42", myo.name)
        assertEquals("11:22:33:44:55:66", myo.address)
    }
}