package it.ncorti.emgvisualizer.ui.export

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.ncorti.emgvisualizer.MyoApplication
import it.ncorti.emgvisualizer.dagger.DeviceManager
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class ExportPresenter(val view: ExportContract.View) : ExportContract.Presenter {

    @Inject
    lateinit var deviceManager: DeviceManager

    private val counter: AtomicInteger = AtomicInteger()
    private val buffer: ArrayList<FloatArray> = arrayListOf()

    var dataSubscription: Disposable? = null

    init {
        MyoApplication.applicationComponent.inject(this)
    }

    override fun start() {
        view.showCollectedPoints(counter.get())
    }

    override fun stop() {
        dataSubscription?.dispose()
    }

    override fun onCollectionTogglePressed() {
        deviceManager.myo?.apply {
            if (this.isStreaming()) {
                if (dataSubscription == null || dataSubscription?.isDisposed == true) {
                    dataSubscription = this.dataFlowable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe {
                                view.showCollectionStarted()
                            }
                            .subscribe {
                                buffer.add(it)
                                view.showCollectedPoints(counter.incrementAndGet())
                            }
                } else {
                    dataSubscription?.dispose()
                    view.enableResetButton()
                    view.showSaveArea()
                    view.showCollectionStopped()
                }
            } else {
                // Not Streaming
            }
        }
    }

    override fun onResetPressed() {
        counter.set(0)
        view.showCollectedPoints(0)
        dataSubscription?.dispose()
        view.hideSaveArea()
        view.enableResetButton()
    }

    override fun onSavePressed() {
        println("Save pressed!!! ${counter.get()}")
        buffer.forEach {
            println(it)
        }
    }

    override fun onSharePressed() {
        println("Share pressed!!! ${counter.get()}")
        buffer.forEach {
            println(it)
        }
    }
}