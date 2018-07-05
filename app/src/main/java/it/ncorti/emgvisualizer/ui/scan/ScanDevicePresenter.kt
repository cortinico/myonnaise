package it.ncorti.emgvisualizer.ui.scan

import android.bluetooth.BluetoothDevice
import com.ncorti.myonnaise.Myonnaise
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.ncorti.emgvisualizer.MyoApplication
import it.ncorti.emgvisualizer.dagger.DeviceManager
import it.ncorti.emgvisualizer.ui.model.Device
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScanDevicePresenter(val view: ScanDeviceContract.View) : ScanDeviceContract.Presenter {

    @Inject
    lateinit var myonnaise: Myonnaise

    @Inject
    lateinit var deviceManager: DeviceManager

    private lateinit var scanFlowable: Flowable<BluetoothDevice>
    private var scanSubscription: Disposable? = null

    init {
        MyoApplication.applicationComponent.inject(this)
    }

    override fun create() {
        scanFlowable = myonnaise.startScan(10, TimeUnit.SECONDS)
    }

    override fun start() {
        view.wipeDeviceList()
        if (deviceManager.scannedDeviceList.isEmpty()) {
            view.showStartMessage()
        } else {
            view.populateDeviceList(deviceManager
                    .scannedDeviceList
                    .map { it -> Device(it.name, it.address) })
        }
    }

    override fun stop() {
        scanSubscription?.dispose()
        view.hideScanLoading()
    }

    override fun onScanToggleClicked() {
        if (scanSubscription?.isDisposed == false) {
            scanSubscription?.dispose()
            view.hideScanLoading()
            if (deviceManager.scannedDeviceList.isEmpty()){
                view.showEmptyListMessage()
            }
        } else {
            view.hideEmptyListMessage()
            view.showScanLoading()
            scanSubscription = scanFlowable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it !in deviceManager.scannedDeviceList) {
                            view.addDeviceToList(Device(it.name, it.address))
                            deviceManager.scannedDeviceList.add(it)
                        }
                    }, {
                        view.hideScanLoading()
                        view.showScanError()
                        if (deviceManager.scannedDeviceList.isEmpty()){
                            view.showEmptyListMessage()
                        }
                    }, {
                        view.hideScanLoading()
                        view.showScanCompleted()
                        if (deviceManager.scannedDeviceList.isEmpty()){
                            view.showEmptyListMessage()
                        }
                    })
        }
    }

    override fun onDeviceSelected(index: Int) {
        deviceManager.selectedIndex = index
        view.navigateToControlDevice()
    }
}