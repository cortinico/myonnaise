package it.ncorti.emgvisualizer.ui.graph

import com.ncorti.myonnaise.Myo
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import it.ncorti.emgvisualizer.dagger.DeviceManager
import it.ncorti.emgvisualizer.ui.testutil.TestSchedulerRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify

class GraphPresenterTest {

    @get:Rule
    val testSchedulerRule = TestSchedulerRule()

    private lateinit var mockedView: GraphContract.View
    private lateinit var mockedDeviceManager: DeviceManager
    private lateinit var mockedMyo: Myo
    private lateinit var testPresenter: GraphPresenter

    @Before
    fun setUp() {
        mockedView = mock {}
        mockedMyo = mock {}
        mockedDeviceManager = mock {
            on(mock.myo) doReturn mockedMyo
        }

        testPresenter = GraphPresenter(mockedView, mockedDeviceManager)
    }

    @Test
    fun onStart_withDeviceNotStreaming_showMessage() {
        whenever(mockedDeviceManager.myo?.isStreaming()).thenReturn(false)

        testPresenter.start()

        verify(mockedView).showNoStreamingMessage()
    }

    @Test
    fun onStart_withDeviceStreaming_hideTheErrorMessage() {
        whenever(mockedDeviceManager.myo?.isStreaming()).thenReturn(true)
        whenever(mockedDeviceManager.myo?.dataFlowable()).thenReturn(Flowable.empty())

        testPresenter.start()

        verify(mockedView).hideNoStreamingMessage()
    }

    @Test
    fun onStart_withDeviceStreaming_startTheGraph() {
        whenever(mockedDeviceManager.myo?.isStreaming()).thenReturn(true)
        whenever(mockedDeviceManager.myo?.dataFlowable()).thenReturn(Flowable.empty())

        testPresenter.start()

        verify(mockedView).startGraph(true)
    }

    @Test
    fun onStart_withDeviceStreaming_populateTheGraph() {
        whenever(mockedDeviceManager.myo?.isStreaming()).thenReturn(true)
        whenever(mockedDeviceManager.myo?.dataFlowable())
            .thenReturn(
                Flowable.just(
                    floatArrayOf(1.0f),
                    floatArrayOf(2.0f),
                    floatArrayOf(3.0f)
                )
            )

        testPresenter.start()

        verify(mockedView).showData(floatArrayOf(1.0f))
        verify(mockedView).showData(floatArrayOf(2.0f))
        verify(mockedView).showData(floatArrayOf(3.0f))
    }

    @Test
    fun onStop_stopTheGraph() {
        testPresenter.stop()

        verify(mockedView).startGraph(false)
    }
}
