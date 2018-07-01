package it.ncorti.emgvisualizer

import android.bluetooth.BluetoothDevice
import com.ncorti.myonnaise.Myo

class DeviceManager {

    var selectedIndex: Int = -1
        set(value) {
            if (value != field) { myo = null }
            field = value
        }

    var scannedDeviceList: MutableList<BluetoothDevice> = mutableListOf()

    var myo: Myo? = null

    var connected = myo?.isConnected() ?: false
}