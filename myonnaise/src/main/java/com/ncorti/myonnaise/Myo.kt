package com.ncorti.myonnaise

import android.bluetooth.*
import android.content.Context
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import io.reactivex.processors.ReplayProcessor
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.TimeUnit

private const val MYO_MIN_VALUE = -120f
private const val MYO_MAX_VALUE = 120f

/** Service ID - MYO CONTROL  */
private val SERVICE_CONTROL_ID = UUID.fromString("d5060001-a904-deb9-4748-2c7f4a124842")
/** Service ID - MYO DATA  */
private val SERVICE_EMG_DATA_ID = UUID.fromString("d5060005-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - Myo Information  */
private val CHAR_INFO_ID = UUID.fromString("d5060101-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - Myo Firmware */
private val CHAR_FIRMWARE_ID = UUID.fromString("d5060201-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - Command ID  */
private val CHAR_COMMAND_ID = UUID.fromString("d5060401-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - EMG Sample 0  */
private val CHAR_EMG_0_ID = UUID.fromString("d5060105-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - EMG Sample 1  */
private val CHAR_EMG_1_ID = UUID.fromString("d5060205-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - EMG Sample 2  */
private val CHAR_EMG_2_ID = UUID.fromString("d5060305-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - EMG Sample 3  */
private val CHAR_EMG_3_ID = UUID.fromString("d5060405-a904-deb9-4748-2c7f4a124842")
private const val CHAR_EMG_POSTFIX = "05-a904-deb9-4748-2c7f4a124842"
/**
 * Android Characteristic ID
 * (from Android Samples/BluetoothLeGatt/SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG)
 */
private val CHAR_CLIENT_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

/** Emg Byte Size  */
private const val EMG_ARRAY_SIZE = 16
private const val PROCESSOR_BUFFER_SIZE = 120000
private const val MYO_MAX_FREQUENCY = 200

private const val TAG = "MYO"

enum class MyoStatus {
    CONNECTED, CONNECTING, DISCONNECTED
}

enum class MyoControlStatus {
    STREAMING, NOT_STREAMING
}

class Myo(private val device: BluetoothDevice) : BluetoothGattCallback() {

    var frequency: Int = 0
        set(value) {
            field = if (value >= MYO_MAX_FREQUENCY) 0 else value
        }

    private val connectionStatusSubject: BehaviorSubject<MyoStatus> = BehaviorSubject.createDefault(MyoStatus.DISCONNECTED)
    private val controlStatusSubject: BehaviorSubject<MyoControlStatus> = BehaviorSubject.createDefault(MyoControlStatus.NOT_STREAMING)
    private val dataProcessor: PublishProcessor<FloatArray> = PublishProcessor.create()

    private var byteReader = ByteReader()
    private var gatt: BluetoothGatt? = null
    private var serviceControl: BluetoothGattService? = null
    private var characteristicCommand: BluetoothGattCharacteristic? = null
    private var characteristicInfo: BluetoothGattCharacteristic? = null
    private var serviceEmg: BluetoothGattService? = null
    private var characteristicEmg0: BluetoothGattCharacteristic? = null
    private var characteristicEmg1: BluetoothGattCharacteristic? = null
    private var characteristicEmg2: BluetoothGattCharacteristic? = null
    private var characteristicEmg3: BluetoothGattCharacteristic? = null

    private val writeQueue: LinkedList<BluetoothGattDescriptor> = LinkedList()
    private val readQueue: LinkedList<BluetoothGattCharacteristic> = LinkedList()

    fun statusObservable(): Observable<MyoStatus> = connectionStatusSubject
    fun controlObservable(): Observable<MyoControlStatus> = controlStatusSubject

    fun dataFlowable(): Flowable<FloatArray> {
        return if (frequency == 0){
            dataProcessor
        } else {
            dataProcessor.sample((1000/frequency).toLong(), TimeUnit.MILLISECONDS)
        }
    }

    fun connect(context: Context) {
        connectionStatusSubject.onNext(MyoStatus.CONNECTING)
        gatt = device.connectGatt(context, false, this)
    }

    fun disconnect() {
        gatt?.disconnect()
        controlStatusSubject.onNext(MyoControlStatus.NOT_STREAMING)
        connectionStatusSubject.onNext(MyoStatus.DISCONNECTED)
    }

    fun isConnected() = connectionStatusSubject.value == MyoStatus.CONNECTED

    fun isStreaming() = controlStatusSubject.value == MyoControlStatus.STREAMING

    fun sendCommand(command: Command): Boolean {
        characteristicCommand?.apply {
            this.value = command
            if (this.properties == BluetoothGattCharacteristic.PROPERTY_WRITE) {
                if (command.isStartStreamingCommand()) {
                    controlStatusSubject.onNext(MyoControlStatus.STREAMING)
                } else if (command.isStopStreamingCommand()) {
                    controlStatusSubject.onNext(MyoControlStatus.NOT_STREAMING)
                }
                gatt?.writeCharacteristic(this)
                return true
            }
        }
        return false
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        Log.d(TAG, "onConnectionStateChange: $status -> $newState")
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d(TAG, "Bluetooth Connected")
            connectionStatusSubject.onNext(MyoStatus.CONNECTED)
            gatt.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // Calling disconnect() here will cause to release the GATT resources.
            disconnect()
            controlStatusSubject.onNext(MyoControlStatus.NOT_STREAMING)
            connectionStatusSubject.onNext(MyoStatus.DISCONNECTED)
            Log.d(TAG, "Bluetooth Disconnected")
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Log.d(TAG, "onServicesDiscovered received: $status")

        if (status != BluetoothGatt.GATT_SUCCESS) {
            return
        }

        // Find GATT Service EMG
        serviceEmg = gatt.getService(SERVICE_EMG_DATA_ID)
        serviceEmg?.apply {
            characteristicEmg0 = serviceEmg?.getCharacteristic(CHAR_EMG_0_ID)
            characteristicEmg1 = serviceEmg?.getCharacteristic(CHAR_EMG_1_ID)
            characteristicEmg2 = serviceEmg?.getCharacteristic(CHAR_EMG_2_ID)
            characteristicEmg3 = serviceEmg?.getCharacteristic(CHAR_EMG_3_ID)

            val emgCharacteristics = listOf(
                    characteristicEmg0,
                    characteristicEmg1,
                    characteristicEmg2,
                    characteristicEmg3)

            emgCharacteristics.forEach { emgCharacteristic ->
                emgCharacteristic?.apply {
                    if (gatt.setCharacteristicNotification(emgCharacteristic, true)) {
                        val descriptor = emgCharacteristic.getDescriptor(CHAR_CLIENT_CONFIG)
                        descriptor?.apply {
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            writeDescriptor(gatt, descriptor)
                        }
                    }
                }
            }
        }

        // Find GATT Service Control
        serviceControl = gatt.getService(SERVICE_CONTROL_ID)
        serviceControl?.apply {
            characteristicInfo = this.getCharacteristic(CHAR_INFO_ID)
            characteristicInfo?.apply {
                // if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the
                // callback above. GIVE PRECEDENCE to descriptor writes. They must all finish first.
                readQueue.add(this)
                if (readQueue.size == 1 && writeQueue.size == 0) {
                    gatt.readCharacteristic(this)
                }
            }
            characteristicCommand = this.getCharacteristic(CHAR_COMMAND_ID)
            characteristicCommand?.apply {
                sendCommand(CommandList.unSleep())
            }
        }
    }

    private fun writeDescriptor(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor) {
        writeQueue.add(descriptor)
        if (writeQueue.size == 1) {
            gatt.writeDescriptor(descriptor)
        }
    }


    override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
        super.onDescriptorWrite(gatt, descriptor, status)
        Log.d(TAG, "onDescriptorWrite status: $status")
        writeQueue.remove()
        //if there is more to write, do it!
        if (writeQueue.size > 0)
            gatt.writeDescriptor(writeQueue.element())
        else if (readQueue.size > 0)
            gatt.readCharacteristic(readQueue.element())
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        readQueue.remove()
        Log.d(TAG, "onCharacteristicRead status: $status ${characteristic.uuid}")

        if (CHAR_INFO_ID == characteristic.uuid) {
            // Myo Device Information
            val data = characteristic.value
            if (data != null && data.isNotEmpty()) {
                val byteReader = ByteReader()
                byteReader.byteData = data
                // TODO Show this to the user
                val callbackMsg = String.format("Serial Number     : %02x:%02x:%02x:%02x:%02x:%02x",
                        byteReader.byte, byteReader.byte, byteReader.byte,
                        byteReader.byte, byteReader.byte, byteReader.byte) +
                        '\n'.toString() + String.format("Unlock            : %d", byteReader.short) +
                        '\n'.toString() + String.format("Classifier builtin:%d active:%d (have:%d)",
                        byteReader.byte, byteReader.byte, byteReader.byte) +
                        '\n'.toString() + String.format("Stream Type       : %d", byteReader.byte)
                Log.d(TAG, "MYO info string: $callbackMsg")
            }
        }

        if (readQueue.size > 0)
            gatt.readCharacteristic(readQueue.element())
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        super.onCharacteristicChanged(gatt, characteristic)
//        Log.d(TAG, "onCharacteristicChanged ${characteristic.uuid}")
        if (characteristic.uuid.toString().endsWith(CHAR_EMG_POSTFIX)) {
            val emgData = characteristic.value
            byteReader.byteData = emgData
            dataProcessor.onNext(byteReader.getBytes(EMG_ARRAY_SIZE))
        }
//         TODO Check if we need to send unsleep again.
    }
}