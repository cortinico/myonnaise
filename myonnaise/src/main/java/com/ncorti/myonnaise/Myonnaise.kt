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

class Myonnaise(val context: Context) {

    private val blManager = context.getSystemService(Activity.BLUETOOTH_SERVICE) as BluetoothManager
    private val blAdapter = blManager.adapter
    private val blLowEnergyScanner = blAdapter.bluetoothLeScanner

    private var scanCallback: MyonnaiseScanCallback? = null

    fun startScan(): Flowable<BluetoothDevice> {
        val scanFlowable: Flowable<BluetoothDevice> = Flowable.create({
            scanCallback = MyonnaiseScanCallback(it)
            blLowEnergyScanner.startScan(scanCallback)
        }, BackpressureStrategy.BUFFER)
        return scanFlowable.doOnCancel {
            blLowEnergyScanner.stopScan(scanCallback)
        }
    }

    fun startScan(interval: Long, timeUnit: TimeUnit): Flowable<BluetoothDevice> =
            startScan().takeUntil(Flowable.timer(interval, timeUnit))

    fun getMyo(bluetoothDevice: BluetoothDevice): Myo {
        return Myo(bluetoothDevice)
    }

    fun getMyo(myoAddress: String): Single<Myo> {
        return Single.create {
            val filter = ScanFilter.Builder()
                    .setDeviceAddress(myoAddress)
                    .build()
            val settings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build()
            blLowEnergyScanner.startScan(listOf(filter), settings, object : ScanCallback() {
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