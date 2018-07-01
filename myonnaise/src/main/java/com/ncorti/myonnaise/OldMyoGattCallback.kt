package com.ncorti.myonnaise

import android.bluetooth.*
import android.util.Log
import java.util.*

/**
 * Class involving Callbacks for GATT Bluetooth profile
 *
 * Please note that we are perfroming signal sampling @25 Hz
 *
 * @author Nicola
 */
class OldMyoGattCallback
/**
 * Generic public constructor
 * @param sensor Sensor reference
 */
(
        /** Reference to Myo sensor  */
        private val concreteSensor: OldMyoSensor) : BluetoothGattCallback() {
    /** TAG for debugging purpose  */
    private val TAG = "MyoGatt"
    /** Bytereader for reading from raw data  */
    private val br: ByteReader
    /** Queue for writing descriptors  */
    private var writeDescriptorQueue: Queue<BluetoothGattDescriptor> = LinkedList()
    /** Queue for reading characteristics  */
    private var readCharacteristicQueue: Queue<BluetoothGattCharacteristic> = LinkedList()
    /** Reference to Bluethoot GATT Profile  */
    private var mBluetoothGatt: BluetoothGatt? = null
    /** Reference to Characteristic Command  */
    private var mCharacteristic_command: BluetoothGattCharacteristic? = null
    /** Reference to Characteristic EMG 0  */
    private var mCharacteristic_emg0: BluetoothGattCharacteristic? = null

    /** Last time sent NEVER_SLEEP to Myo  */
    private var lastNeverSleepTime = System.currentTimeMillis()

    //    /** Reference to Characteristic EMG 1 */
    //    private BluetoothGattCharacteristic mCharacteristic_emg1;
    //    /** Reference to Characteristic EMG 2 */
    //    private BluetoothGattCharacteristic mCharacteristic_emg2;
    //    /** Reference to Characteristic EMG 3 */
    //    private BluetoothGattCharacteristic mCharacteristic_emg3;

    /** EMG Command List  */
//    private val commandList = CommandList()

    init {
        br = ByteReader()
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        Log.d(TAG, "onConnectionStateChange: $status -> $newState")
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // GATT Connected
            // Searching GATT Service
            gatt.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // GATT Disconnected
            stopCallback()
            Log.d(TAG, "Bluetooth Disconnected")
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Log.d(TAG, "onServicesDiscovered received: $status")

        if (status == BluetoothGatt.GATT_SUCCESS) {
            // Find GATT Service EMG
            val service_emg = gatt.getService(UUID.fromString(MYO_EMG_DATA_ID))
            if (service_emg == null) {
                Log.d(TAG, "No Myo EMG-Data Service !!")
            } else {
                Log.d(TAG, "Find Myo EMG-Data Service !!")
                // Getting CommandCharacteristic
                mCharacteristic_emg0 = service_emg.getCharacteristic(UUID.fromString(EMG_0_ID))
                //                mCharacteristic_emg1 = service_emg.getCharacteristic(UUID.fromString(EMG_1_ID));
                //                mCharacteristic_emg2 = service_emg.getCharacteristic(UUID.fromString(EMG_2_ID));
                //                mCharacteristic_emg3 = service_emg.getCharacteristic(UUID.fromString(EMG_3_ID));
                //                if (mCharacteristic_emg0 == null || mCharacteristic_emg1 == null ||
                //                        mCharacteristic_emg2 == null || mCharacteristic_emg3 == null) {
                //                    Log.d(TAG, "Not Found EMG-Data Characteristic");
                if (mCharacteristic_emg0 == null) {
                    Log.d(TAG, "Not Found EMG-Data Characteristic")
                } else {
                    // Setting the notification
                    val registered_0 = gatt.setCharacteristicNotification(mCharacteristic_emg0, true)
                    //                    boolean registered_1 = gatt.setCharacteristicNotification(mCharacteristic_emg1, true);
                    //                    boolean registered_2 = gatt.setCharacteristicNotification(mCharacteristic_emg2, true);
                    //                    boolean registered_3 = gatt.setCharacteristicNotification(mCharacteristic_emg3, true);
                    if (!registered_0) {
                        Log.d(TAG, "EMG-Data Notification FALSE !!")
                    } else {
                        Log.d(TAG, "EMG-Data Notification TRUE !!")
                        // Turn ON the Characteristic Notification
                        val descriptor_0 = mCharacteristic_emg0!!.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG))
                        if (descriptor_0 != null) {
                            descriptor_0.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE

                            writeGattDescriptor(descriptor_0)

                            Log.d(TAG, "Set descriptor")

                        } else {
                            Log.d(TAG, "No descriptor")
                        }
                    }
                }
            }

            // Find GATT Service Control
            val service_control = gatt.getService(UUID.fromString(MYO_CONTROL_ID))
            if (service_control == null) {
                Log.d(TAG, "No Myo Control Service !!")
            } else {
                Log.d(TAG, "Find Myo Control Service !!")
                // Get the MyoInfoCharacteristic
                val characteristic = service_control.getCharacteristic(UUID.fromString(MYO_INFO_ID))
                if (characteristic == null) {
                    Log.d(TAG, "Empty Characteristic")
                } else {
                    Log.d(TAG, "Find read Characteristic !!")
                    //put the characteristic into the read queue
                    readCharacteristicQueue.add(characteristic)
                    //if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the callback above
                    //GIVE PRECEDENCE to descriptor writes.  They must all finish first.
                    if (readCharacteristicQueue.size == 1 && writeDescriptorQueue.size == 0) {
                        mBluetoothGatt!!.readCharacteristic(characteristic)
                    }
                }
                // Get CommandCharacteristic
                mCharacteristic_command = service_control.getCharacteristic(UUID.fromString(COMMAND_ID))
                if (mCharacteristic_command == null) {
                    Log.d(TAG, "Not found command Characteristic")
                } else {
                    Log.d(TAG, "Find command Characteristic !!")
                    // set Myo [Never Sleep Mode]
//                    setMyoControlCommand(commandList.sendUnSleep())
                }
            }
        }
    }

    /**
     * Method for handling queuing of write descriptor operations
     * @param d Gatt Descriptor to be written
     */
    private fun writeGattDescriptor(d: BluetoothGattDescriptor) {
        //put the descriptor into the write queue
        writeDescriptorQueue.add(d)

        //if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
        if (writeDescriptorQueue.size == 1) {
            mBluetoothGatt!!.writeDescriptor(d)
        }

        Log.d(TAG, "Write Descriptor")
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.")
        } else {
            Log.d(TAG, "Callback: Error writing GATT Descriptor: $status")
        }
        writeDescriptorQueue.remove()  //pop the item that we just finishing writing
        //if there is more to write, do it!
        if (writeDescriptorQueue.size > 0)
            mBluetoothGatt!!.writeDescriptor(writeDescriptorQueue.element())
        else if (readCharacteristicQueue.size > 0)
            mBluetoothGatt!!.readCharacteristic(readCharacteristicQueue.element())
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        readCharacteristicQueue.remove()

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (UUID.fromString(FIRMWARE_ID) == characteristic.uuid) {
                // Myo Firmware Infomation
                val data = characteristic.value
                if (data != null && data.size > 0) {
                    val byteReader = ByteReader()
                    byteReader.byteData = data
                    Log.d(TAG, String.format("MYO Version is %d.%d.%d - %d",
                            byteReader.short, byteReader.short,
                            byteReader.short, byteReader.short))
                }
                if (data == null) {
                    Log.d(TAG, "Characteristic String is " + characteristic.toString())
                }
            } else if (UUID.fromString(MYO_INFO_ID) == characteristic.uuid) {
                // Myo Device Information
                val data = characteristic.value
                if (data != null && data.size > 0) {
                    val byteReader = ByteReader()
                    byteReader.byteData = data

                    val callback_msg = String.format("Serial Number     : %02x:%02x:%02x:%02x:%02x:%02x",
                            byteReader.byte, byteReader.byte, byteReader.byte,
                            byteReader.byte, byteReader.byte, byteReader.byte) +
                            '\n'.toString() + String.format("Unlock            : %d", byteReader.short) +
                            '\n'.toString() + String.format("Classifier builtin:%d active:%d (have:%d)",
                            byteReader.byte, byteReader.byte, byteReader.byte) +
                            '\n'.toString() + String.format("Stream Type       : %d", byteReader.byte)

                    Log.d(TAG, "MYO info string: $callback_msg")
                }
            }
        } else {
            Log.d(TAG, "onCharacteristicRead error: $status")
        }
        if (readCharacteristicQueue.size > 0)
            mBluetoothGatt!!.readCharacteristic(readCharacteristicQueue.element())
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        //        if (EMG_0_ID.equals(characteristic.getUuid().toString()) ||
        //                EMG_1_ID.equals(characteristic.getUuid().toString()) ||
        //                EMG_2_ID.equals(characteristic.getUuid().toString()) ||
        //                EMG_3_ID.equals(characteristic.getUuid().toString())) {
        if (EMG_0_ID == characteristic.uuid.toString()) {
            //long systemTime_us = System.nanoTime();
            val systemTime_ms = System.currentTimeMillis()
            val emg_data = characteristic.value

            br.byteData = emg_data
            val data = br.getBytes(MYO_EMG_BYTE_SIZE)
            val point = RawDataPoint(systemTime_ms, MYO_EMG_ACCURACY, data)
            concreteSensor.addDataPoint(point)

            if (systemTime_ms > lastNeverSleepTime + NEVER_SLEEP_SEND_TIME) {
                // set Myo [Never Sleep Mode]
//                setMyoControlCommand(commandList.sendUnSleep())
                lastNeverSleepTime = systemTime_ms
            }
        }
    }

    /**
     * Setter for new Bluethoot GATT, already connected
     * @param gatt Bluethoot GATT already connected
     */
    fun setBluetoothGatt(gatt: BluetoothGatt?) {
        mBluetoothGatt = gatt
    }

    /**
     * Send a new control command to Myo
     * @param command A byte array containing myo commands
     * @return True if command have been delivered successfully
     */
    fun setMyoControlCommand(command: ByteArray?): Boolean {
        if (mCharacteristic_command != null) {
            mCharacteristic_command!!.value = command
            val i_prop = mCharacteristic_command!!.properties
            if (i_prop == BluetoothGattCharacteristic.PROPERTY_WRITE) {
                if (mBluetoothGatt!!.writeCharacteristic(mCharacteristic_command)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Method for closing the GATT connection
     */
    fun stopCallback() {
        // Before the closing GATT, set Myo [Normal Sleep Mode].
//        setMyoControlCommand(commandList.sendNormalSleep())
        Log.d(TAG, "STOP CALLBACK")

        writeDescriptorQueue = LinkedList()
        readCharacteristicQueue = LinkedList()
        if (mCharacteristic_command != null) {
            mCharacteristic_command = null
        }
        if (mCharacteristic_emg0 != null) {
            mCharacteristic_emg0 = null
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt = null
        }
    }

    companion object {

        /** Service ID - MYO CONTROL  */
        private val MYO_CONTROL_ID = "d5060001-a904-deb9-4748-2c7f4a124842"
        /** Service ID - MYO DATA  */
        private val MYO_EMG_DATA_ID = "d5060005-a904-deb9-4748-2c7f4a124842"
        /** Characteristics ID - Myo Information  */
        private val MYO_INFO_ID = "d5060101-a904-deb9-4748-2c7f4a124842"
        //    /** Characteristics ID - Myo Firmware */
        private val FIRMWARE_ID = "d5060201-a904-deb9-4748-2c7f4a124842"
        /** Characteristics ID - Command ID  */
        private val COMMAND_ID = "d5060401-a904-deb9-4748-2c7f4a124842"
        /** Characteristics ID - EMG Sample 0  */
        private val EMG_0_ID = "d5060105-a904-deb9-4748-2c7f4a124842"

        //    /** Characteristics ID - EMG Sample 1 */
        //    private static final String EMG_1_ID = "d5060205-a904-deb9-4748-2c7f4a124842";
        //    /** Characteristics ID - EMG Sample 2 */
        //    private static final String EMG_2_ID = "d5060305-a904-deb9-4748-2c7f4a124842";
        //    /** Characteristics ID - EMG Sample 3 */
        //    private static final String EMG_3_ID = "d5060405-a904-deb9-4748-2c7f4a124842";


        /** android Characteristic ID (from Android Samples/BluetoothLeGatt/SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG)  */
        private val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
        /** Device measurment characteristic  */
        private val MYO_EMG_ACCURACY = 1
        /** Emg Byte Size  */
        private val MYO_EMG_BYTE_SIZE = 8
        /** Never Sleep Send Time Delay  */
        private val NEVER_SLEEP_SEND_TIME: Long = 10000
    }
}
