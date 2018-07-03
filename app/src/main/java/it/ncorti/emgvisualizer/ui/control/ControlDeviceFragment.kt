package it.ncorti.emgvisualizer.ui.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
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

        button_connect.setOnClickListener { presenter.onConnectionToggleClicked() }
        button_start_streaming.setOnClickListener { presenter.onStreamingToggleClicked() }
        button_vibrate_1.setOnClickListener { presenter.onVibrateClicked(1) }
        button_vibrate_2.setOnClickListener { presenter.onVibrateClicked(2) }
        button_vibrate_3.setOnClickListener { presenter.onVibrateClicked(3) }
        seekbar_frequency.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter.onProgressSelected(progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        seekbar_frequency.isEnabled = false
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
        } else {
            presenter.stop()
        }
    }

    override fun showDeviceInformation(name: String?, address: String) {
        device_name.text = name ?: getString(R.string.unknown_device)
        device_address.text = address
    }

    override fun showConnectionProgress() {
        progress_connect.animate().alpha(1.0f)
    }

    override fun hideConnectionProgress() {
        progress_connect.animate().alpha(0.0f)
    }

    override fun showConnecting() {
        device_status.text = getString(R.string.connecting)
    }

    override fun showConnected() {
        device_status.text = getString(R.string.connected)
        button_connect.text = getString(R.string.disconnect)
    }

    override fun showDisconnected() {
        device_status.text = getString(R.string.disconnected)
        button_connect.text = getString(R.string.connect)
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

    override fun disableControlPanel() {
        button_start_streaming.isEnabled = false
        button_vibrate_1.isEnabled = false
        button_vibrate_2.isEnabled = false
        button_vibrate_3.isEnabled = false
        seekbar_frequency.isEnabled = false
    }

    override fun enableControlPanel() {
        button_start_streaming.isEnabled = true
        button_vibrate_1.isEnabled = true
        button_vibrate_2.isEnabled = true
        button_vibrate_3.isEnabled = true
        seekbar_frequency.isEnabled = true
    }

    override fun showStreaming() {
        button_start_streaming?.text = getText(R.string.stop)
        device_streaming_status?.text = getString(R.string.currently_streaming)
    }

    override fun showNotStreaming() {
        button_start_streaming?.text = getText(R.string.start)
        device_streaming_status?.text = getString(R.string.not_streaming)
    }

    override fun showFrequency(frequency: Int) {
        device_frequency_value.text = getString(R.string.templated_hz, frequency)
    }
}