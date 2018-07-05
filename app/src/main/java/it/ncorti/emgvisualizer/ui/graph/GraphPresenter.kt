package it.ncorti.emgvisualizer.ui.graph

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.ncorti.emgvisualizer.MyoApplication
import it.ncorti.emgvisualizer.dagger.DeviceManager
import javax.inject.Inject

class GraphPresenter(val view: GraphContract.View) : GraphContract.Presenter {

    @Inject
    lateinit var deviceManager: DeviceManager

    private var dataSubscription: Disposable? = null

    init {
        MyoApplication.applicationComponent.inject(this)
    }

    override fun create() { }

    override fun start() {
        deviceManager.myo?.apply {
            if (this.isStreaming()) {
                view.hideNoStreamingMessage()
                dataSubscription?.apply {
                    if (!this.isDisposed) this.dispose()
                }
                dataSubscription = this.dataFlowable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            view.startGraph(true)
                        }
                        .subscribe {
                            view.showData(it)
                        }
            } else {
                view.showNoStreamingMessage()
            }
        }
    }

    override fun stop() {
        view.startGraph(false)
        dataSubscription?.dispose()
    }
}