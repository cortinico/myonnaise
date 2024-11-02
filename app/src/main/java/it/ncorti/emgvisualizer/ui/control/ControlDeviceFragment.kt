package it.ncorti.emgvisualizer.ui.control

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import it.ncorti.emgvisualizer.BaseFragment
import it.ncorti.emgvisualizer.R
import it.ncorti.emgvisualizer.databinding.LayoutControlDeviceBinding
import javax.inject.Inject

class ControlDeviceFragment : BaseFragment<ControlDeviceContract.Presenter>(), ControlDeviceContract.View {

    private lateinit var binding: LayoutControlDeviceBinding

    companion object {
        fun newInstance() = ControlDeviceFragment()
    }

    @Inject
    lateinit var controlDevicePresenter: ControlDevicePresenter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        attachPresenter(controlDevicePresenter)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutControlDeviceBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        return binding.root
    }

    @Suppress("MagicNumber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonConnect.setOnClickListener { controlDevicePresenter.onConnectionToggleClicked() }
        binding.buttonStartStreaming.setOnClickListener { controlDevicePresenter.onStreamingToggleClicked() }
        binding.buttonVibrate1.setOnClickListener { controlDevicePresenter.onVibrateClicked(1) }
        binding.buttonVibrate2.setOnClickListener { controlDevicePresenter.onVibrateClicked(2) }
        binding.buttonVibrate3.setOnClickListener { controlDevicePresenter.onVibrateClicked(3) }
        binding.seekbarFrequency.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                controlDevicePresenter.onProgressSelected(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        binding.seekbarFrequency.isEnabled = false
    }

    override fun showDeviceInformation(name: String?, address: String) {
        binding.deviceName.text = name ?: getString(R.string.unknown_device)
        binding.deviceAddress.text = address
    }

    override fun showConnecting() {
        binding.deviceStatus.text = getString(R.string.connecting)
    }

    override fun showConnected() {
        binding.deviceStatus.text = getString(R.string.connected)
        binding.buttonConnect.text = getString(R.string.disconnect)
    }

    override fun showDisconnected() {
        binding.deviceStatus.text = getString(R.string.disconnected)
        binding.buttonConnect.text = getString(R.string.connect)
    }

    override fun showConnectionError() {
        Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show()
        binding.buttonConnect.text = getString(R.string.connect)
    }

    override fun enableConnectButton() {
        binding.buttonConnect.isEnabled = true
    }

    override fun disableConnectButton() {
        binding.buttonConnect.isEnabled = false
    }

    override fun disableControlPanel() {
        binding.buttonStartStreaming.isEnabled = false
        binding.buttonVibrate1.isEnabled = false
        binding.buttonVibrate2.isEnabled = false
        binding.buttonVibrate3.isEnabled = false
        binding.seekbarFrequency.isEnabled = false
    }

    override fun enableControlPanel() {
        binding.buttonStartStreaming.isEnabled = true
        binding.buttonVibrate1.isEnabled = true
        binding.buttonVibrate2.isEnabled = true
        binding.buttonVibrate3.isEnabled = true
        binding.seekbarFrequency.isEnabled = true
    }

    override fun showStreaming() {
        binding.buttonStartStreaming.text = getText(R.string.stop)
        binding.deviceStreamingStatus.text = getString(R.string.currently_streaming)
    }

    override fun showNotStreaming() {
        binding.buttonStartStreaming.text = getText(R.string.start)
        binding.deviceStreamingStatus.text = getString(R.string.not_streaming)
    }

    override fun showFrequency(frequency: Int) {
        binding.deviceFrequencyValue.text = getString(R.string.templated_hz, frequency)
    }
}
