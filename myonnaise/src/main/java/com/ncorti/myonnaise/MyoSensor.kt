package com.ncorti.myonnaise

import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.util.Log

import java.util.LinkedList

/**
 * Class that represent the Myo EMG device sensor
 */
class MyoSensor
/**
 * Public constructor that requires
 *
 * @param name      Myo Device name
 * @param hwAddress Myo Bluethoot address
 * @param context   Application context
 */
(
        /**
         * Myo Device Name
         */
        private val myoDeviceName: String,
        /**
         * Myo Device Name
         */
        private val myoDeviceAddress: String,
        /**
         * Reference to Context
         */
        private val context: Context) {

    /**
     * Reference to bluetooth scanner
     */
    private val mBluetoothScanner: BluetoothLeScanner
    /**
     * Bluethoot characteristic
     */
    private var mBluetoothGatt: BluetoothGatt? = null
    /**
     * Reference to GATT callbacks
     */
    private var mMyoCallback: OldMyoGattCallback? = null
    /**
     * Myo available commands
     */
    private val commandList: MyoCommandList
    /**
     * Handler for thread execution
     */
    private val mHandler: Handler
    /**
     * Reference to Scan Callback
     */
    private val mCallback: ScanMyoListCallback

    /**
     * Flag to detect if device is streaming or not
     */
    var isMeasuring = false
        private set

    val isConnected: Boolean
        get() = mBluetoothGatt != null

    val channels: Int
        get() = 8
    /**
     * Data point list
     */
    private val dataPoints = LinkedList<RawDataPoint>()
    /**
     * Max sensor value
     */
    /**
     * Getter for sensor min value
     *
     * @return Sensor min value
     */
    var minValue = Integer.MAX_VALUE.toFloat()
        private set
    /**
     * Min sensor value
     */
    /**
     * Getter for sensor max value
     *
     * @return Sensor max value
     */
    var maxValue = Integer.MIN_VALUE.toFloat()
        private set

    /**
     * Sensor name
     */
    /**
     * Getter for sensor name
     *
     * @return Sensor name
     */
    val name: String? = null

    /**
     * Method for getting short sensor name, used for Arff file (it's short name without spaces)
     *
     * @return Sensor short name
     */
    val shortName: String
        get() {
            var shortName = name!!.substring(0, 5).toLowerCase().replace(" ", "_")
            for (i in shortName.length..4)
                shortName += "_"
            return shortName
        }

    /**
     * Getter for sensor status string (for displaying purposes
     *
     * @return Sensor status string
     */
    //status += "Received RAW points: <b>" + getDataPoints().size() + "</b></p>";
    val statusString: String
        get() {
            var status = "<p>Device is currenty: "
            if (isConnected) {
                status += " <b>Connected.</b><br>"
            } else {
                status += " <b>Disconnected.</b><br>"
            }
            if (isMeasuring)
                status += " Raw data receiving is: <b>Active.</b><br>"
            else
                status += " Raw data receiving is: <b>Inactive.</b><br>"
            status += "</p>"
            return status
        }

    init {

        val mBluetoothManager = context.getSystemService(Activity.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothScanner = mBluetoothManager.adapter.bluetoothLeScanner

        mCallback = ScanMyoListCallback()
        mHandler = Handler()
        this.commandList = MyoCommandList()
    }

    fun startConnection() {
        mHandler.postDelayed({
            mBluetoothScanner.stopScan(mCallback)
            //                EventBusProvider.postOnMainThread(new SensorConnectEvent(MyoSensor.this, false));
        }, SCAN_PERIOD)
        mBluetoothScanner.startScan(mCallback)
    }

    fun stopConnection() {
        closeBLEGatt()
    }

    fun startMeasurement() {
        if (mBluetoothGatt == null || !mMyoCallback!!.setMyoControlCommand(commandList.sendEmgOnly())) {
            Log.d(TAG, "Error start measurment")
        } else {
            isMeasuring = true
            mMyoCallback!!.setMyoControlCommand(commandList.sendVibration3())
        }
    }

    fun stopMeasurement() {
        if (mBluetoothGatt == null || !mMyoCallback!!.setMyoControlCommand(commandList.sendUnsetData())) {
            Log.d(TAG, "Error pause measurment")
        } else {
            isMeasuring = false
        }
    }

    protected inner class ScanMyoListCallback : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            if (myoDeviceName == device.name) {
                mBluetoothScanner.stopScan(mCallback)
                // Trying to connect GATT
                mMyoCallback = OldMyoGattCallback(this@MyoSensor)
                mBluetoothGatt = device.connectGatt(context, false, mMyoCallback)
                mMyoCallback!!.setBluetoothGatt(mBluetoothGatt)
                //                EventBusProvider.postOnMainThread(new SensorConnectEvent(MyoSensor.this, true));
            }
        }
    }

    /**
     * Private method for clossing GATT Profile connection
     */
    private fun closeBLEGatt() {
        if (mBluetoothGatt == null) {
            return
        }
        mMyoCallback!!.stopCallback()
        mBluetoothGatt!!.close()
        mBluetoothGatt = null
    }

    /**
     * Getter for data point list.
     * The list is cloned before returning.
     *
     * @return Data point list
     */
    @Synchronized
    fun getDataPoints(): LinkedList<RawDataPoint> {
        return dataPoints.clone() as LinkedList<RawDataPoint>
    }

    /**
     * Method for adding a point to the sensor data point list
     *
     * @param dataPoint New point to be added
     */
    @Synchronized
    fun addDataPoint(dataPoint: RawDataPoint) {
        dataPoints.addLast(dataPoint)

        if (dataPoints.size > MAX_DATA_POINTS) {
            dataPoints.removeFirst()
        }

        // Check for new range event
        var newLimits = false

        for (value in dataPoint.values) {
            if (value > maxValue) {
                maxValue = value
                newLimits = true
            }
            if (value < minValue) {
                minValue = value
                newLimits = true
            }
        }

        if (newLimits) {
            Log.d(TAG, "New range for sensor $name: $minValue - $maxValue")
            //            EventBusProvider.postOnMainThread(getRangeEvent(minValue, maxValue));
        }
        //        SensorUpdateEvent updateEvent = getUpdateEvent(dataPoint);
        //        EventBusProvider.postOnMainThread(updateEvent);
    }

    /**
     * Method for clearing data point list
     */
    @Synchronized
    fun clearDataPoints() {
        dataPoints.clear()
    }

    companion object {

        /**
         * TAG for debugging purpose
         */
        private val TAG = "MyoSensor"
        /**
         * Accelerometer sensor name
         */
        private val SENSOR_NAME = "Myo EMG Sensor"
        /**
         * Myo emg actual min value
         */
        private val MYO_MIN_VALUE = -120f
        /**
         * Myo emg actual max value
         */
        private val MYO_MAX_VALUE = 120f

        /**
         * Max Scan period in ms
         */
        private val SCAN_PERIOD: Long = 5000

        /**
         * Data point list size
         */
        private val MAX_DATA_POINTS = 5000
    }
}
