package it.ncorti.emgvisualizer.ui.control

import it.ncorti.emgvisualizer.BasePresenter
import it.ncorti.emgvisualizer.BaseView
import it.ncorti.emgvisualizer.Device


interface ControlDeviceContract {

    interface View : BaseView<Presenter> {

        fun showDeviceInformation(device: Device)

        fun showConnectionProgress()

        fun hideConnectionProgress()

        fun showConnectionSuccess()

        fun showConnectionError()

        fun enableConnectButton()

        fun disableConnectButton()

    }

    interface Presenter : BasePresenter {

        fun onConnectClicked()

    }
}