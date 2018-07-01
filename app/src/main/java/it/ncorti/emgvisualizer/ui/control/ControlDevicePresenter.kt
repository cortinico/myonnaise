package it.ncorti.emgvisualizer.ui.control

import com.ncorti.myonnaise.CommandList
import com.ncorti.myonnaise.MyoControlStatus
import com.ncorti.myonnaise.MyoStatus
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

    private var statusSubscription: Disposable? = null
    private var controlSubscription: Disposable? = null

    init {
        MyoApplication.applicationComponent.inject(this)
    }

    override fun stop() {
        statusSubscription?.dispose()
        controlSubscription?.dispose()
    }

    override fun start() {
        if (deviceManager.selectedIndex == -1) {
            view.disableConnectButton()
            return
        }

        val selectedDevice = deviceManager.scannedDeviceList[deviceManager.selectedIndex]

        view.showDeviceInformation(selectedDevice.name, selectedDevice.address)
        view.enableConnectButton()

        if (deviceManager.myo == null) {
            deviceManager.myo = myonnaise.getMyo(selectedDevice)
        }

        deviceManager.myo?.apply {
            statusSubscription =
                    this.statusObservable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                when (it) {
                                    MyoStatus.CONNECTED -> {
                                        view.hideConnectionProgress()
                                        view.showConnected()
                                        view.enableControlPanel()
                                    }
                                    MyoStatus.CONNECTING -> {
                                        view.showConnectionProgress()
                                        view.showConnecting()
                                    }
                                    else -> {
                                        view.hideConnectionProgress()
                                        view.showDisconnected()
                                        view.disableControlPanel()
                                    }
                                }
                            }
            controlSubscription =
                    this.controlObservable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                if (it == MyoControlStatus.STREAMING) {
                                    view.showStreaming()
                                } else {
                                    view.showNotStreaming()
                                }
                            }
        }
    }

    override fun onConnectionToggleClicked() {
        deviceManager.myo?.apply {
            if (!this.isConnected()) {
                this.connect(myonnaise.context)
            } else {
                this.disconnect()
            }
        }
    }

    override fun onStreamingToggleClicked() {
        deviceManager.myo?.apply {
            if (!this.isStreaming()) {
                this.sendCommand(CommandList.emgFilteredOnly())
            } else {
                this.sendCommand(CommandList.stopStreaming())
            }
        }
    }

    override fun onVibrateClicked(duration: Int) {
        deviceManager.myo?.apply {
            this.sendCommand(when (duration) {
                1 -> CommandList.vibration1()
                2 -> CommandList.vibration2()
                else -> CommandList.vibration3()
            })
        }
    }

}