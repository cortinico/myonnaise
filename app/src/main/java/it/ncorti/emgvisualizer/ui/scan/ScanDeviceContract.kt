package it.ncorti.emgvisualizer.ui.scan

import it.ncorti.emgvisualizer.BasePresenter
import it.ncorti.emgvisualizer.BaseView
import it.ncorti.emgvisualizer.ui.model.Device


interface ScanDeviceContract {

    interface View : BaseView<Presenter> {

        fun wipeDeviceList()

        fun addDeviceToList(device: Device)

        fun populateDeviceList(list: List<Device>)

        fun showScanLoading()

        fun hideScanLoading()

        fun showScanError()

        fun showScanCompleted()

        fun navigateToControlDevice()
    }

    interface Presenter : BasePresenter {

        fun onScanToggleClicked()

        fun onDeviceSelected(index: Int)
    }
}