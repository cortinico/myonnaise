package it.ncorti.emgvisualizer.ui.control

import android.bluetooth.BluetoothDevice
import com.ncorti.myonnaise.CommandList
import com.ncorti.myonnaise.MYO_MAX_FREQUENCY
import com.ncorti.myonnaise.Myo
import com.ncorti.myonnaise.MyoControlStatus
import com.ncorti.myonnaise.MyoStatus
import com.ncorti.myonnaise.Myonnaise
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import it.ncorti.emgvisualizer.dagger.DeviceManager
import it.ncorti.emgvisualizer.ui.testutil.TestSchedulerRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ControlDevicePresenterTest {

    @get:Rule
    val testSchedulerRule = TestSchedulerRule()

    private lateinit var mockedView: ControlDeviceContract.View
    private lateinit var mockedDeviceManager: DeviceManager
    private lateinit var mockedMyo: Myo
    private lateinit var mockedBluetoothDevice: BluetoothDevice
    private lateinit var mockedMyonnaise: Myonnaise
    private lateinit var testPresenter: ControlDevicePresenter

    @Before
    fun setUp() {

        mockedView = mock {}
        mockedMyo = mock {
            on(this.mock.statusObservable()) doReturn Observable.empty()
            on(this.mock.controlObservable()) doReturn Observable.empty()
        }
        mockedMyonnaise = mock {}
        mockedBluetoothDevice = mock {
            on(this.mock.name) doReturn "42"
            on(this.mock.address) doReturn "aa:bb:cc:dd:ee:ff"
        }
        mockedDeviceManager = mock {
            on(this.mock.myo) doReturn mockedMyo
            on(this.mock.selectedIndex) doReturn 0
            on(this.mock.scannedDeviceList) doReturn mutableListOf(mockedBluetoothDevice)
        }

        testPresenter = ControlDevicePresenter(mockedView, mockedMyonnaise, mockedDeviceManager)
    }

    @Test
    fun onStart_withNoSelectedDevice_disableConnect() {
        whenever(mockedDeviceManager.selectedIndex).doReturn(-1)

        testPresenter.start()

        verify(mockedView).disableConnectButton()
    }

    @Test
    fun onStart_withSelectedDevice_enableConnect() {
        testPresenter.start()

        verify(mockedView).enableConnectButton()
        verify(mockedView).showDeviceInformation("42", "aa:bb:cc:dd:ee:ff")
    }

    @Test
    fun onConnectingEvent_showConnectingProgress() {
        whenever(mockedMyo.statusObservable()).thenReturn(Observable.just(MyoStatus.CONNECTING))

        testPresenter.start()
        verify(mockedView).showConnecting()
    }

    @Test
    fun onConnectedEvent_showConnected() {
        whenever(mockedMyo.statusObservable()).thenReturn(Observable.just(MyoStatus.CONNECTED))

        testPresenter.start()

        verify(mockedView).showConnected()
    }

    @Test
    fun onReadyEvent_showControlPanel() {
        whenever(mockedMyo.statusObservable()).thenReturn(Observable.just(MyoStatus.READY))

        testPresenter.start()

        verify(mockedView).enableControlPanel()
    }

    @Test
    fun onStreamingEvent_showStreaming() {
        whenever(mockedMyo.controlObservable()).thenReturn(Observable.just(MyoControlStatus.STREAMING))

        testPresenter.start()

        verify(mockedView).showStreaming()
    }

    @Test
    fun onNotStreamingEvent_showNotStreaming() {
        whenever(mockedMyo.controlObservable()).thenReturn(Observable.just(MyoControlStatus.NOT_STREAMING))

        testPresenter.start()

        verify(mockedView, atLeast(1)).showNotStreaming()
    }

    @Test
    fun onStop_hideLoading() {
        testPresenter.stop()

        if (testPresenter.statusSubscription != null) {
            verify(testPresenter.statusSubscription)?.dispose()
        }
        if (testPresenter.controlSubscription != null) {
            verify(testPresenter.controlSubscription)?.dispose()
        }
    }

    @Test
    fun onConnectionToggleClicked_withConnectedMyo_disconnect() {
        whenever(mockedMyo.isConnected()).thenReturn(true)

        testPresenter.onConnectionToggleClicked()

        verify(mockedMyo).disconnect()
    }

    @Test
    fun onConnectionToggleClicked_withDisconnectedMyo_connect() {
        whenever(mockedMyo.isConnected()).thenReturn(false)

        testPresenter.onConnectionToggleClicked()

        verify(mockedMyo).connect(anyOrNull())
    }

    @Test
    fun onStreamingToggleClicked_withStreamingMyo_stopStreaming() {
        whenever(mockedMyo.isStreaming()).thenReturn(true)

        testPresenter.onStreamingToggleClicked()

        verify(mockedMyo).sendCommand(CommandList.stopStreaming())
    }

    @Test
    fun onStreamingToggleClicked_withNotStreamingMyo_startStreaming() {
        whenever(mockedMyo.isConnected()).thenReturn(false)

        testPresenter.onStreamingToggleClicked()

        verify(mockedMyo).sendCommand(CommandList.emgFilteredOnly())
    }

    @Test
    fun onVibrateClicked_withShortVibration_sendShortVibrationCommand() {
        testPresenter.onVibrateClicked(1)

        verify(mockedMyo).sendCommand(CommandList.vibration1())
    }

    @Test
    fun onVibrateClicked_withMediumVibration_sendMediumVibrationCommand() {
        testPresenter.onVibrateClicked(2)

        verify(mockedMyo).sendCommand(CommandList.vibration2())
    }

    @Test
    fun onVibrateClicked_withLongVibration_sendLongVibrationCommand() {
        testPresenter.onVibrateClicked(3)

        verify(mockedMyo).sendCommand(CommandList.vibration3())
    }

    @Test
    fun onProgressSelected_showAndUpdateFrequency() {
        testPresenter.onProgressSelected(0)

        verify(mockedView).showFrequency(1)
        verify(mockedMyo).frequency = 1
    }

    @Test
    fun onProgressSelected_withMaxFrequency_showAndUpdateFrequency() {
        testPresenter.onProgressSelected(7)

        verify(mockedView).showFrequency(MYO_MAX_FREQUENCY)
        verify(mockedMyo).frequency = MYO_MAX_FREQUENCY
    }
}
