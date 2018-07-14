package it.ncorti.emgvisualizer.ui.scan

import android.bluetooth.BluetoothDevice
import com.ncorti.myonnaise.Myo
import com.ncorti.myonnaise.Myonnaise
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import it.ncorti.emgvisualizer.dagger.DeviceManager
import it.ncorti.emgvisualizer.ui.model.Device
import it.ncorti.emgvisualizer.ui.testutil.TestSchedulerRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.RuntimeException

class ScanDevicePresenterTest {

    @get:Rule
    val testSchedulerRule = TestSchedulerRule()

    private lateinit var mockedView: ScanDeviceContract.View
    private lateinit var mockedDeviceManager: DeviceManager
    private lateinit var mockedMyo: Myo
    private lateinit var mockedBluetoothDevice: BluetoothDevice
    private lateinit var mockedMyonnaise: Myonnaise
    private lateinit var testPresenter: ScanDevicePresenter

    @Before
    fun setUp() {
        mockedView = mock {}
        mockedMyo = mock {}
        mockedMyonnaise = mock {}
        mockedBluetoothDevice = mock {
            on(mock.name) doReturn "42"
            on(mock.address) doReturn "aa:bb:cc:dd:ee:ff"
        }
        mockedDeviceManager = mock {
            on(mock.myo) doReturn mockedMyo
        }

        testPresenter = ScanDevicePresenter(mockedView, mockedMyonnaise, mockedDeviceManager)
    }

    @Test
    fun onStart_withEmptyScannedDevice_showEmptyList() {
        whenever(mockedDeviceManager.scannedDeviceList).thenReturn(mutableListOf())

        testPresenter.start()

        verify(mockedView).wipeDeviceList()
        verify(mockedView).showStartMessage()
    }

    @Test
    fun onStart_withAlreadyScannedDevice_populateDeviceList() {
        whenever(mockedDeviceManager.scannedDeviceList).thenReturn(mutableListOf(mockedBluetoothDevice))

        testPresenter.start()

        verify(mockedView).wipeDeviceList()
        verify(mockedView).populateDeviceList(listOf(Device(mockedBluetoothDevice.name, mockedBluetoothDevice.address)))
    }

    @Test
    fun onStop_hideLoading() {
        testPresenter.stop()

        verify(mockedView).hideScanLoading()
    }

    @Test
    fun onScanToggleClicked_withAlreadyScanning_stopScanning() {
        // We simulate that we are subscribed to a flowable
        testPresenter.scanSubscription = mock {}

        testPresenter.onScanToggleClicked()

        verify(testPresenter.scanSubscription)?.dispose()
        verify(mockedView).hideScanLoading()
    }


    @Test
    fun onScanToggleClicked_withAlreadyScanningNoDeviceFound_showEmptyMessage() {
        whenever(mockedDeviceManager.scannedDeviceList).thenReturn(mutableListOf())
        // We simulate that we are subscribed to a flowable
        testPresenter.scanSubscription = mock {}

        testPresenter.onScanToggleClicked()

        verify(mockedView).showEmptyListMessage()
    }

    @Test
    fun onScanToggleClicked_withNotScanning_startScanning() {
        testPresenter.scanSubscription = null
        testPresenter.scanFlowable = Flowable.empty()

        testPresenter.onScanToggleClicked()

        verify(mockedView).hideEmptyListMessage()
        verify(mockedView).showScanLoading()
    }

    @Test
    fun onScanToggleClicked_duringScan_addDeviceToList() {
        testPresenter.scanSubscription = null
        testPresenter.scanFlowable = Flowable.just(mockedBluetoothDevice)

        testPresenter.onScanToggleClicked()

        verify(mockedView).hideEmptyListMessage()
        verify(mockedView).showScanLoading()
        verify(mockedView).addDeviceToList(Device(mockedBluetoothDevice.name, mockedBluetoothDevice.address))
    }

    @Test
    fun onScanToggleClicked_scanComplete_showScanComplete() {
        whenever(mockedDeviceManager.scannedDeviceList).thenReturn(mutableListOf())
        testPresenter.scanSubscription = null
        testPresenter.scanFlowable = Flowable.empty()

        testPresenter.onScanToggleClicked()

        verify(mockedView).hideScanLoading()
        verify(mockedView).showScanCompleted()
        verify(mockedView).showEmptyListMessage()
    }

    @Test
    fun onScanToggleClicked_withScanError_showErrorAndStopScan() {
        whenever(mockedDeviceManager.scannedDeviceList).thenReturn(mutableListOf())
        testPresenter.scanSubscription = null
        testPresenter.scanFlowable = Flowable.error(RuntimeException())

        testPresenter.onScanToggleClicked()

        verify(mockedView).hideScanLoading()
        verify(mockedView).showScanError()
        verify(mockedView).showEmptyListMessage()
    }

    @Test
    fun onDeviceSelected_navigateToControlDevice() {
        testPresenter.onDeviceSelected(0)

        assertEquals(0, mockedDeviceManager.selectedIndex)
        verify(mockedView).navigateToControlDevice()
    }

}