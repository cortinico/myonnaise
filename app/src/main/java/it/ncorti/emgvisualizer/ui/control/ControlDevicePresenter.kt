package it.ncorti.emgvisualizer.ui.control

import com.ncorti.myonnaise.Myonnaise
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.ncorti.emgvisualizer.DeviceManager
import it.ncorti.emgvisualizer.MyoApplication
import javax.inject.Inject

class ControlDevicePresenter(val view: ControlDeviceContract.View) : ControlDeviceContract.Presenter {

    @Inject
    lateinit var myonnaise: Myonnaise

    @Inject
    lateinit var deviceManager: DeviceManager

    private var connectSubscription: Disposable? = null

    init {
        MyoApplication.applicationComponent.inject(this)
    }

    override fun stop() {
        connectSubscription?.dispose()
        connectSubscription = null
    }

    override fun start() {
        val device = deviceManager.selectedDevice

        if (device != null) {
            view.showDeviceInformation(device)
            view.enableConnectButton()
        } else {
            view.disableConnectButton()
        }
    }

    override fun onConnectClicked() {
        val device = deviceManager.selectedDevice
        if (connectSubscription != null && connectSubscription?.isDisposed == false) {
            return
        }
        if (device == null)
            return

        view.showConnectionProgress()
        connectSubscription = myonnaise.connect(device.address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.showConnectionSuccess()
                    view.hideConnectionProgress()
                }, {
                    view.showConnectionError()
                    view.hideConnectionProgress()
                })

    }

}