package it.ncorti.emgvisualizer.ui.control

import it.ncorti.emgvisualizer.BasePresenter
import it.ncorti.emgvisualizer.BaseView


interface ControlDeviceContract {

    interface View : BaseView<Presenter> {

        fun showDeviceInformation(name: String?, address: String)

        fun showConnectionProgress()

        fun hideConnectionProgress()

        fun showConnected()

        fun showDisconnected()

        fun showConnecting()

        fun showConnectionError()

        fun enableConnectButton()

        fun disableConnectButton()

        fun enableControlPanel()

        fun disableControlPanel()

        fun showStreaming()

        fun showNotStreaming()
    }

    interface Presenter : BasePresenter {

        fun onConnectionToggleClicked()

        fun onStreamingToggleClicked()

        fun onVibrateClicked(duration: Int)

    }
}