package it.ncorti.emgvisualizer.ui.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import it.ncorti.emgvisualizer.Device
import it.ncorti.emgvisualizer.R
import kotlinx.android.synthetic.main.layout_control_device.*


class ControlDeviceFragment : Fragment(), ControlDeviceContract.View {

    private lateinit var presenter: ControlDeviceContract.Presenter

    companion object {
        fun newInstance() = ControlDeviceFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.layout_control_device, container, false)

        setHasOptionsMenu(true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_connect.setOnClickListener { presenter.onConnectClicked() }
    }

    override fun setPresenter(presenter: ControlDeviceContract.Presenter) {
        this.presenter = presenter
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            presenter.start()
        }
    }

    override fun showDeviceInformation(device: Device) {
        device_name.text = device.name ?: getString(R.string.unknown_device)
        device_address.text = device.address
    }

    override fun showConnectionProgress() {
        progress_connect.animate().alpha(1.0f)
    }

    override fun hideConnectionProgress() {
        progress_connect.animate().alpha(0.0f)
    }

    override fun showConnectionSuccess() {
        Toast.makeText(context, "Connection success", Toast.LENGTH_SHORT).show()
        button_connect.text = getString(R.string.disconnect)
    }

    override fun showConnectionError() {
        Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show()
        button_connect.text = getString(R.string.connect)
    }

    override fun enableConnectButton() {
        button_connect.isEnabled = true
    }

    override fun disableConnectButton() {
        button_connect.isEnabled = false
    }
}