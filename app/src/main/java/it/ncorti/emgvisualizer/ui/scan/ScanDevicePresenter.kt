package it.ncorti.emgvisualizer.ui.scan

import com.ncorti.myonnaise.Myonnaise
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

    init {
        MyoApplication.applicationComponent.inject(this)
    }

    private val scanFlowable = myonnaise.startScan(10, TimeUnit.SECONDS)
    private var scanSubscription: Disposable? = null

    override fun start() {
        view.wipeDeviceList()
        view.populateDeviceList(deviceManager
                .scannedDeviceList
                .map { it -> Device(it.name, it.address) })
    }

    override fun stop() {
        scanSubscription?.dispose()
        view.hideScanLoading()
    }

    override fun onScanToggleClicked() {
        if (scanSubscription?.isDisposed == false) {
            scanSubscription?.dispose()
            view.hideScanLoading()
        } else {
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
                    }, {
                        view.hideScanLoading()
                        view.showScanCompleted()
                    })
        }
    }

    override fun onDeviceSelected(index: Int) {
        deviceManager.selectedIndex = index
        view.navigateToControlDevice()
    }
}