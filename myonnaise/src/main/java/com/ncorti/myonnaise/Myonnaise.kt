package com.ncorti.myonnaise

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Single
import java.util.concurrent.TimeUnit

/**
 * Entry point to the Myonnaise library.
 * Use this class to do a Bluetooth scan and search for a new [Myo].
 *
 * Please note that in order to perform a Bluetooth Scan, the user needs to provide the
 * [android.permission.ACCESS_COARSE_LOCATION] permission. You must request this permission to
 * the user otherwise your scan will be empty.
 */
class Myonnaise(val context: Context) {

    private val blManager = context.getSystemService(Activity.BLUETOOTH_SERVICE) as BluetoothManager
    private val blAdapter = blManager.adapter
    private val blLowEnergyScanner = blAdapter?.bluetoothLeScanner

    private var scanCallback: MyonnaiseScanCallback? = null

    /**
     * Use this method to perform a scan. This method will return a [Flowable] that will publish
     * all the found [BluetoothDevice].
     * The scan will be stopped when you cancel the Flowable.
     * To set a timeout use the overloaded method
     *
     * Usage:
     * ```
     * Myonnaise(context).startScan()
     *                      .subscribeOn(Schedulers.io())
     *                      .observeOn(AndroidSchedulers.mainThread())
     *                      .subscribe({
     *                          // Do something with the found device
     *                          println(it.address)
     *                      })
     * ```
     * @return A flowable that will publish the found [BluetoothDevice]
     */
    fun startScan(): Flowable<BluetoothDevice> {
        val scanFlowable: Flowable<BluetoothDevice> = Flowable.create({
            scanCallback = MyonnaiseScanCallback(it)
            blLowEnergyScanner?.startScan(scanCallback)
        }, BackpressureStrategy.BUFFER)
        return scanFlowable.doOnCancel {
            blLowEnergyScanner?.stopScan(scanCallback)
        }
    }

    /**
     * Use this method to perform a scan. This method will return a [Flowable] that will publish
     * all the found [BluetoothDevice] and will stop after the timeout.
     *
     * Usage:
     * ```
     * Myonnaise(context).startScan(5, TimeUnit.MINUTES)
     *                      .subscribeOn(Schedulers.io())
     *                      .observeOn(AndroidSchedulers.mainThread())
     *                      .subscribe({
     *                          // Do something with the found device.
     *                          println(it.address)
     *                      })
     * ```
     * @param
     * @param interval the timeout value.
     * @param timeUnit time units to use for [interval].
     */
    fun startScan(interval: Long, timeUnit: TimeUnit): Flowable<BluetoothDevice> =
            startScan().takeUntil(Flowable.timer(interval, timeUnit))

    /**
     * Returns a [Myo] from a [BluetoothDevice]. Use this method after you discovered a device with
     * the [startScan] method.
     */
    fun getMyo(bluetoothDevice: BluetoothDevice): Myo {
        return Myo(bluetoothDevice)
    }

    /**
     * Returns a [Myo] from a Bluetooth address. Please note that this method will perform another
     * scan to search for the desired device and return a [Single].
     */
    fun getMyo(myoAddress: String): Single<Myo> {
        return Single.create {
            val filter = ScanFilter.Builder()
                    .setDeviceAddress(myoAddress)
                    .build()
            val settings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build()
            blLowEnergyScanner?.startScan(listOf(filter), settings, object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    result?.device?.apply {
                        it.onSuccess(Myo(this))
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    it.onError(RuntimeException())
                }
            })
        }
    }

    inner class MyonnaiseScanCallback(private val emitter: FlowableEmitter<BluetoothDevice>) : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.apply { emitter.onNext(this) }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            emitter.onError(RuntimeException())
        }
    }
}