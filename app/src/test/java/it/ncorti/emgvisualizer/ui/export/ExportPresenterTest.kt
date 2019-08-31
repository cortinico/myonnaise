package it.ncorti.emgvisualizer.ui.export

import com.ncorti.myonnaise.Myo
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import it.ncorti.emgvisualizer.dagger.DeviceManager
import it.ncorti.emgvisualizer.ui.testutil.TestSchedulerRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt

class ExportPresenterTest {

    @get:Rule
    val testSchedulerRule = TestSchedulerRule()

    private lateinit var mockedView: ExportContract.View
    private lateinit var mockedDeviceManager: DeviceManager
    private lateinit var mockedMyo: Myo
    private lateinit var testPresenter: ExportPresenter

    @Before
    fun setUp() {
        mockedView = mock {}
        mockedMyo = mock {}
        mockedDeviceManager = mock {
            on(mock.myo) doReturn mockedMyo
        }

        testPresenter = ExportPresenter(mockedView, mockedDeviceManager)
    }

    @Test
    fun onStart_collectedPointsAreShown() {
        testPresenter.start()

        verify(mockedView).showCollectedPoints(anyInt())
    }

    @Test
    fun onStart_withStreamingDevice_enableCollection() {
        whenever(mockedDeviceManager.myo?.isStreaming()).thenReturn(true)
        testPresenter.start()

        verify(mockedView).enableStartCollectingButton()
    }

    @Test
    fun onStart_withNotStreamingDevice_disableCollection() {
        whenever(mockedDeviceManager.myo?.isStreaming()).thenReturn(false)
        testPresenter.start()

        verify(mockedView).disableStartCollectingButton()
    }

    @Test
    fun onCollectionTogglePressed_withNotStreamingDevice_showErrorMessage() {
        testPresenter.onCollectionTogglePressed()

        verify(mockedView).showNotStreamingErrorMessage()
    }

    @Test
    fun onCollectionTogglePressed_withStreamingDeviceAndNotSubscribed_showCollectedPoints() {
        whenever(mockedDeviceManager.myo?.isStreaming()).thenReturn(true)
        whenever(mockedDeviceManager.myo?.dataFlowable())
            .thenReturn(
                Flowable.just(
                    floatArrayOf(1.0f),
                    floatArrayOf(2.0f),
                    floatArrayOf(3.0f)
                )
            )

        testPresenter.dataSubscription = null
        testPresenter.onCollectionTogglePressed()

        assertNotNull(testPresenter.dataSubscription)
        verify(mockedView).showCollectionStarted()
        verify(mockedView).disableResetButton()
        verify(mockedView).showCollectedPoints(1)
        verify(mockedView).showCollectedPoints(2)
        verify(mockedView).showCollectedPoints(3)
    }

    @Test
    fun onCollectionTogglePressed_withStreamingDevice_showCollectedPoints() {
        whenever(mockedDeviceManager.myo?.isStreaming()).thenReturn(true)

        // We simulate that we are subscribed to a flowable
        testPresenter.dataSubscription = mock {}
        testPresenter.onCollectionTogglePressed()

        verify(testPresenter.dataSubscription)?.dispose()
        verify(mockedView).enableResetButton()
        verify(mockedView).showSaveArea()
        verify(mockedView).showCollectionStopped()
    }

    @Test
    fun onResetPressed_resetTheView() {
        testPresenter.onResetPressed()

        verify(mockedView).showCollectedPoints(0)
        verify(mockedView).hideSaveArea()
        verify(mockedView).disableResetButton()
    }

    @Test
    fun onSavePressed_askViewToSave() {
        testPresenter.onSavePressed()

        verify(mockedView).saveCsvFile(ArgumentMatchers.anyString())
    }

    @Test
    fun onSharePressed_askViewToShare() {
        testPresenter.onSharePressed()

        verify(mockedView).sharePlainText(ArgumentMatchers.anyString())
    }

    @Test
    fun createCsv_withEmptyBuffer_EmptyList() {
        assertEquals("", testPresenter.createCsv(arrayListOf()))
    }

    @Test
    fun createCsv_withSingleLine_OneLineCsv() {
        val buffer = arrayListOf(floatArrayOf(1f, 2f, 3f))
        assertEquals("1.0;2.0;3.0;\n", testPresenter.createCsv(buffer))
    }

    @Test
    fun createCsv_withMultipleLine_MultipleLineCsv() {
        val buffer = arrayListOf(
            floatArrayOf(1f, 2f, 3f),
            floatArrayOf(4f, 5f, 6f)
        )
        assertEquals("1.0;2.0;3.0;\n4.0;5.0;6.0;\n", testPresenter.createCsv(buffer))
    }
}