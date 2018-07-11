package it.ncorti.emgvisualizer.ui.export

import it.ncorti.emgvisualizer.dagger.DeviceManager
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class ExportPresenterTest {

    lateinit var mockedView: ExportContract.View

    lateinit var mockedDeviceManager : DeviceManager

    lateinit var testPresenter : ExportPresenter

    @Before
    fun setUp() {
        mockedView = mock(ExportContract.View::class.java)
        mockedDeviceManager = mock(DeviceManager::class.java)

        testPresenter = ExportPresenter(mockedView, mockedDeviceManager)
    }

    @Test
    fun onCollectionTogglePressed_sampleTest() {
        testPresenter.onResetPressed()

        verify(mockedView).showCollectedPoints(0)
    }

    @Test
    fun getDeviceManager() {
    }

    @Test
    fun setDeviceManager() {
    }

    @Test
    fun getDataSubscription() {
    }

    @Test
    fun setDataSubscription() {
    }

    @Test
    fun create() {
    }

    @Test
    fun start() {
    }

    @Test
    fun stop() {
    }

    @Test
    fun onCollectionTogglePressed() {
    }

    @Test
    fun onResetPressed() {
    }

    @Test
    fun onSavePressed() {
    }

    @Test
    fun onSharePressed() {
    }

    @Test
    fun getView() {
    }
}