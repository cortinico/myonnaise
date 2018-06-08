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
package it.ncorti.emgvisualizer.myo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import it.ncorti.emgvisualizer.model.EventBusProvider;
import it.ncorti.emgvisualizer.model.RawDataPoint;
import it.ncorti.emgvisualizer.model.Sensor;
import it.ncorti.emgvisualizer.model.SensorConnectEvent;
import it.ncorti.emgvisualizer.model.SensorMeasuringEvent;
import it.ncorti.emgvisualizer.model.SensorRangeEvent;
import it.ncorti.emgvisualizer.model.SensorUpdateEvent;

/**
 * Class that represent the Myo EMG device sensor
 * @author Nicola
 */
public class MyoSensor extends Sensor {

    /**
     * TAG for debugging purpose
     */
    private static final String TAG = "MyoSensor";
    /**
     * Accelerometer sensor name
     */
    private static final String SENSOR_NAME = "Myo EMG Sensor";
    /**
     * Myo emg actual min value
     */
    private static final float MYO_MIN_VALUE = -120;
    /**
     * Myo emg actual max value
     */
    private static final float MYO_MAX_VALUE = 120;

    /**
     * Reference to bluetooth scanner
     */
    private BluetoothLeScanner mBluetoothScanner;
    /**
     * Bluethoot characteristic
     */
    private BluetoothGatt mBluetoothGatt;
    /**
     * Reference to GATT callbacks
     */
    private MyoGattCallback mMyoCallback;
    /**
     * Myo available commands
     */
    private MyoCommandList commandList;
    /**
     * Handler for thread execution
     */
    private Handler mHandler;
    /**
     * Myo Device Name
     */
    private String myoDeviceName;
    /**
     * Myo Device Name
     */
    private String myoDeviceAddress;
    /**
     * Reference to Context
     */
    private Context context;
    /**
     * Reference to Scan Callback
     */
    private ScanMyoListCallback mCallback;

    /**
     * Max Scan period in ms
     */
    private static final long SCAN_PERIOD = 5000;

    /**
     * Flag to detect if device is streaming or not
     */
    private boolean measuring = false;

    /**
     * Public constructor that requires
     * @param name      Myo Device name
     * @param hwAddress Myo Bluethoot address
     * @param context   Application context
     */
    public MyoSensor(String name, String hwAddress, Context context) {
        super(name + " - " + hwAddress, MYO_MIN_VALUE, MYO_MAX_VALUE);

        BluetoothManager mBluetoothManager = (BluetoothManager) context.getSystemService(Activity.BLUETOOTH_SERVICE);
        mBluetoothScanner = mBluetoothManager.getAdapter().getBluetoothLeScanner();

        mCallback = new ScanMyoListCallback();
        mHandler = new Handler();
        this.myoDeviceName = name;
        this.myoDeviceAddress = hwAddress;
        this.context = context;
        this.commandList = new MyoCommandList();
    }

    @Override
    public void startConnection() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothScanner.stopScan(mCallback);
                EventBusProvider.postOnMainThread(new SensorConnectEvent(MyoSensor.this, false));
            }
        }, SCAN_PERIOD);
        mBluetoothScanner.startScan(mCallback);
    }

    @Override
    public void stopConnection() {
        closeBLEGatt();
    }

    @Override
    public boolean isConnected() {
        return (mBluetoothGatt != null);
    }

    @Override
    public void startMeasurement() {
        if (mBluetoothGatt == null ||
                !mMyoCallback.setMyoControlCommand(commandList.sendEmgOnly())) {
            Log.d(TAG, "Error start measurment");
            EventBusProvider.postOnMainThread(new SensorMeasuringEvent(this, false));
        } else {
            measuring = true;
            mMyoCallback.setMyoControlCommand(commandList.sendVibration3());
            EventBusProvider.postOnMainThread(new SensorMeasuringEvent(this, true));
        }
    }

    @Override
    public void stopMeasurement() {
        if (mBluetoothGatt == null ||
                !mMyoCallback.setMyoControlCommand(commandList.sendUnsetData())) {
            Log.d(TAG, "Error pause measurment");
            EventBusProvider.postOnMainThread(new SensorMeasuringEvent(this, true));
        } else {
            measuring = false;
            EventBusProvider.postOnMainThread(new SensorMeasuringEvent(this, false));
        }
    }

    @Override
    public boolean isMeasuring() {
        return measuring;
    }

    @Override
    public SensorUpdateEvent getUpdateEvent(RawDataPoint point) {
        return new MyoSensorUpdateEvent(this, point);
    }

    @Override
    public SensorRangeEvent getRangeEvent(float minValue, float maxValue) {
        return new MyoSensorRangeEvent(this, minValue, maxValue);
    }

    @Override
    public int getChannels() {
        return 8;
    }

    protected class ScanMyoListCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (myoDeviceName.equals(device.getName())) {
                mBluetoothScanner.stopScan(mCallback);
                // Trying to connect GATT
                mMyoCallback = new MyoGattCallback(MyoSensor.this);
                mBluetoothGatt = device.connectGatt(context, false, mMyoCallback);
                mMyoCallback.setBluetoothGatt(mBluetoothGatt);
                EventBusProvider.postOnMainThread(new SensorConnectEvent(MyoSensor.this, true));
            }
        }
    }

    /**
     * Private method for clossing GATT Profile connection
     */
    private void closeBLEGatt() {
        if (mBluetoothGatt == null) {
            return;
        }
        mMyoCallback.stopCallback();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
}
