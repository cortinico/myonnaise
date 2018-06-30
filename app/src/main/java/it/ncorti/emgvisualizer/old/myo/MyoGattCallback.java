/* This file is part of EmgVisualizer.

    EmgVisualizer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    EmgVisualizer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with EmgVisualizer.  If not, see <http://www.gnu.org/licenses/>.
*/
package it.ncorti.emgvisualizer.old.myo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import it.ncorti.emgvisualizer.old.model.RawDataPoint;

/**
 * Class involving Callbacks for GATT Bluetooth profile
 *
 * Please note that we are perfroming signal sampling @25 Hz
 *
 * @author Nicola
 */
public class MyoGattCallback extends BluetoothGattCallback {
    /** TAG for debugging purpose */
    private String TAG = "MyoGatt";

    /** Service ID - MYO CONTROL */
    private static final String MYO_CONTROL_ID = "d5060001-a904-deb9-4748-2c7f4a124842";
    /** Service ID - MYO DATA */
    private static final String MYO_EMG_DATA_ID = "d5060005-a904-deb9-4748-2c7f4a124842";
    /** Characteristics ID - Myo Information */
    private static final String MYO_INFO_ID = "d5060101-a904-deb9-4748-2c7f4a124842";
//    /** Characteristics ID - Myo Firmware */
    private static final String FIRMWARE_ID = "d5060201-a904-deb9-4748-2c7f4a124842";
    /** Characteristics ID - Command ID */
    private static final String COMMAND_ID = "d5060401-a904-deb9-4748-2c7f4a124842";
    /** Characteristics ID - EMG Sample 0 */
    private static final String EMG_0_ID = "d5060105-a904-deb9-4748-2c7f4a124842";

//    /** Characteristics ID - EMG Sample 1 */
//    private static final String EMG_1_ID = "d5060205-a904-deb9-4748-2c7f4a124842";
//    /** Characteristics ID - EMG Sample 2 */
//    private static final String EMG_2_ID = "d5060305-a904-deb9-4748-2c7f4a124842";
//    /** Characteristics ID - EMG Sample 3 */
//    private static final String EMG_3_ID = "d5060405-a904-deb9-4748-2c7f4a124842";



    /** android Characteristic ID (from Android Samples/BluetoothLeGatt/SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG) */
    private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    /** Device measurment characteristic */
    private static final int MYO_EMG_ACCURACY = 1;
    /** Emg Byte Size */
    private static final int MYO_EMG_BYTE_SIZE = 8;
    /** Bytereader for reading from raw data */
    private ByteReader br;
    /** Queue for writing descriptors */
    private Queue<BluetoothGattDescriptor> writeDescriptorQueue = new LinkedList<>();
    /** Queue for reading characteristics */
    private Queue<BluetoothGattCharacteristic> readCharacteristicQueue = new LinkedList<>();
    /** Reference to Bluethoot GATT Profile */
    private BluetoothGatt mBluetoothGatt;
    /** Reference to Characteristic Command */
    private BluetoothGattCharacteristic mCharacteristic_command;
    /** Reference to Characteristic EMG 0 */
    private BluetoothGattCharacteristic mCharacteristic_emg0;

    /** Last time sent NEVER_SLEEP to Myo */
    private long lastNeverSleepTime = System.currentTimeMillis();
    /** Never Sleep Send Time Delay */
    private final static long NEVER_SLEEP_SEND_TIME = 10000;

//    /** Reference to Characteristic EMG 1 */
//    private BluetoothGattCharacteristic mCharacteristic_emg1;
//    /** Reference to Characteristic EMG 2 */
//    private BluetoothGattCharacteristic mCharacteristic_emg2;
//    /** Reference to Characteristic EMG 3 */
//    private BluetoothGattCharacteristic mCharacteristic_emg3;

    /** EMG Command List */
    private MyoCommandList commandList = new MyoCommandList();

    /** Reference to Myo sensor */
    private MyoSensor concreteSensor;

    /**
     * Generic public constructor
     * @param sensor Sensor reference
     */
    public MyoGattCallback(MyoSensor sensor) {
        br = new ByteReader();
        this.concreteSensor = sensor;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Log.d(TAG, "onConnectionStateChange: " + status + " -> " + newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // GATT Connected
            // Searching GATT Service
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // GATT Disconnected
            stopCallback();
            Log.d(TAG, "Bluetooth Disconnected");
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        Log.d(TAG, "onServicesDiscovered received: " + status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            // Find GATT Service EMG
            BluetoothGattService service_emg = gatt.getService(UUID.fromString(MYO_EMG_DATA_ID));
            if (service_emg == null) {
                Log.d(TAG, "No Myo EMG-Data Service !!");
            } else {
                Log.d(TAG, "Find Myo EMG-Data Service !!");
                // Getting CommandCharacteristic
                mCharacteristic_emg0 = service_emg.getCharacteristic(UUID.fromString(EMG_0_ID));
//                mCharacteristic_emg1 = service_emg.getCharacteristic(UUID.fromString(EMG_1_ID));
//                mCharacteristic_emg2 = service_emg.getCharacteristic(UUID.fromString(EMG_2_ID));
//                mCharacteristic_emg3 = service_emg.getCharacteristic(UUID.fromString(EMG_3_ID));
//                if (mCharacteristic_emg0 == null || mCharacteristic_emg1 == null ||
//                        mCharacteristic_emg2 == null || mCharacteristic_emg3 == null) {
//                    Log.d(TAG, "Not Found EMG-Data Characteristic");
                if (mCharacteristic_emg0 == null) {
                        Log.d(TAG, "Not Found EMG-Data Characteristic");
                } else {
                    // Setting the notification
                    boolean registered_0 = gatt.setCharacteristicNotification(mCharacteristic_emg0, true);
//                    boolean registered_1 = gatt.setCharacteristicNotification(mCharacteristic_emg1, true);
//                    boolean registered_2 = gatt.setCharacteristicNotification(mCharacteristic_emg2, true);
//                    boolean registered_3 = gatt.setCharacteristicNotification(mCharacteristic_emg3, true);
                    if (!registered_0) {
                        Log.d(TAG, "EMG-Data Notification FALSE !!");
                    } else {
                        Log.d(TAG, "EMG-Data Notification TRUE !!");
                        // Turn ON the Characteristic Notification
                        BluetoothGattDescriptor descriptor_0 = mCharacteristic_emg0.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                        if (descriptor_0 != null) {
                            descriptor_0.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                            writeGattDescriptor(descriptor_0);

                            Log.d(TAG, "Set descriptor");

                        } else {
                            Log.d(TAG, "No descriptor");
                        }
                    }
                }
            }

            // Find GATT Service Control
            BluetoothGattService service_control = gatt.getService(UUID.fromString(MYO_CONTROL_ID));
            if (service_control == null) {
                Log.d(TAG, "No Myo Control Service !!");
            } else {
                Log.d(TAG, "Find Myo Control Service !!");
                // Get the MyoInfoCharacteristic
                BluetoothGattCharacteristic characteristic =
                        service_control.getCharacteristic(UUID.fromString(MYO_INFO_ID));
                if (characteristic == null) {
                    Log.d(TAG, "Empty Characteristic");
                } else {
                    Log.d(TAG, "Find read Characteristic !!");
                    //put the characteristic into the read queue
                    readCharacteristicQueue.add(characteristic);
                    //if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the callback above
                    //GIVE PRECEDENCE to descriptor writes.  They must all finish first.
                    if ((readCharacteristicQueue.size() == 1) && (writeDescriptorQueue.size() == 0)) {
                        mBluetoothGatt.readCharacteristic(characteristic);
                    }
                }
                // Get CommandCharacteristic
                mCharacteristic_command = service_control.getCharacteristic(UUID.fromString(COMMAND_ID));
                if (mCharacteristic_command == null) {
                    Log.d(TAG, "Not found command Characteristic");
                } else {
                    Log.d(TAG, "Find command Characteristic !!");
                    // set Myo [Never Sleep Mode]
                    setMyoControlCommand(commandList.sendUnSleep());
                }
            }
        }
    }

    /**
     * Method for handling queuing of write descriptor operations
     * @param d Gatt Descriptor to be written
     */
    private void writeGattDescriptor(BluetoothGattDescriptor d) {
        //put the descriptor into the write queue
        writeDescriptorQueue.add(d);

        //if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
        if (writeDescriptorQueue.size() == 1) {
            mBluetoothGatt.writeDescriptor(d);
        }

        Log.d(TAG, "Write Descriptor");
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.");
        } else {
            Log.d(TAG, "Callback: Error writing GATT Descriptor: " + status);
        }
        writeDescriptorQueue.remove();  //pop the item that we just finishing writing
        //if there is more to write, do it!
        if (writeDescriptorQueue.size() > 0)
            mBluetoothGatt.writeDescriptor(writeDescriptorQueue.element());
        else if (readCharacteristicQueue.size() > 0)
            mBluetoothGatt.readCharacteristic(readCharacteristicQueue.element());
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        readCharacteristicQueue.remove();

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (UUID.fromString(FIRMWARE_ID).equals(characteristic.getUuid())) {
                // Myo Firmware Infomation
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    ByteReader byteReader = new ByteReader();
                    byteReader.setByteData(data);
                    Log.d(TAG, String.format("MYO Version is %d.%d.%d - %d",
                            byteReader.getShort(), byteReader.getShort(),
                            byteReader.getShort(), byteReader.getShort()));
                }
                if (data == null) {
                    Log.d(TAG,"Characteristic String is " + characteristic.toString());
                }
            } else if (UUID.fromString(MYO_INFO_ID).equals(characteristic.getUuid())) {
                // Myo Device Information
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    ByteReader byteReader = new ByteReader();
                    byteReader.setByteData(data);

                    String callback_msg = String.format("Serial Number     : %02x:%02x:%02x:%02x:%02x:%02x",
                            byteReader.getByte(), byteReader.getByte(), byteReader.getByte(),
                            byteReader.getByte(), byteReader.getByte(), byteReader.getByte()) +
                            '\n' + String.format("Unlock            : %d", byteReader.getShort()) +
                            '\n' + String.format("Classifier builtin:%d active:%d (have:%d)",
                            byteReader.getByte(), byteReader.getByte(), byteReader.getByte()) +
                            '\n' + String.format("Stream Type       : %d", byteReader.getByte());

                    Log.d(TAG, "MYO info string: " + callback_msg);
                }
            }
        }
        else{
            Log.d(TAG, "onCharacteristicRead error: " + status);
        }
        if (readCharacteristicQueue.size() > 0)
            mBluetoothGatt.readCharacteristic(readCharacteristicQueue.element());
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//        if (EMG_0_ID.equals(characteristic.getUuid().toString()) ||
//                EMG_1_ID.equals(characteristic.getUuid().toString()) ||
//                EMG_2_ID.equals(characteristic.getUuid().toString()) ||
//                EMG_3_ID.equals(characteristic.getUuid().toString())) {
        if (EMG_0_ID.equals(characteristic.getUuid().toString())) {
            //long systemTime_us = System.nanoTime();
            long systemTime_ms = System.currentTimeMillis();
            byte[] emg_data = characteristic.getValue();

            br.setByteData(emg_data);
            float[] data = br.getBytes(MYO_EMG_BYTE_SIZE);
            RawDataPoint point = new RawDataPoint(systemTime_ms, MYO_EMG_ACCURACY, data);
            concreteSensor.addDataPoint(point);

            if (systemTime_ms > lastNeverSleepTime + NEVER_SLEEP_SEND_TIME) {
                // set Myo [Never Sleep Mode]
                setMyoControlCommand(commandList.sendUnSleep());
                lastNeverSleepTime = systemTime_ms;
            }
        }
    }

    /**
     * Setter for new Bluethoot GATT, already connected
     * @param gatt Bluethoot GATT already connected
     */
    public void setBluetoothGatt(BluetoothGatt gatt) {
        mBluetoothGatt = gatt;
    }

    /**
     * Send a new control command to Myo
     * @param command A byte array containing myo commands
     * @return True if command have been delivered successfully
     */
    public boolean setMyoControlCommand(byte[] command) {
        if (mCharacteristic_command != null) {
            mCharacteristic_command.setValue(command);
            int i_prop = mCharacteristic_command.getProperties();
            if (i_prop == BluetoothGattCharacteristic.PROPERTY_WRITE) {
                if (mBluetoothGatt.writeCharacteristic(mCharacteristic_command)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method for closing the GATT connection
     */
    public void stopCallback() {
        // Before the closing GATT, set Myo [Normal Sleep Mode].
        setMyoControlCommand(commandList.sendNormalSleep());
        Log.d(TAG, "STOP CALLBACK");

        writeDescriptorQueue = new LinkedList<>();
        readCharacteristicQueue = new LinkedList<>();
        if (mCharacteristic_command != null) {
            mCharacteristic_command = null;
        }
        if (mCharacteristic_emg0 != null) {
            mCharacteristic_emg0 = null;
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt = null;
        }
    }
}
