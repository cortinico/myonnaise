package it.ncorti.emgvisualizer.ui.graph

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.ncorti.emgvisualizer.DeviceManager
import it.ncorti.emgvisualizer.MyoApplication
import javax.inject.Inject

class GraphPresenter(val view: GraphContract.View) : GraphContract.Presenter {

    @Inject
    lateinit var deviceManager: DeviceManager

    private var dataSubscription: Disposable? = null

    init {
        MyoApplication.applicationComponent.inject(this)
    }

    override fun start() {
        deviceManager.myo?.apply {
            if (this.isStreaming()) {
                dataSubscription = this.dataFlowable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            view.showData(it)
                        }
            }
        }
    }

    override fun stop() {
        dataSubscription?.dispose()
    }
}