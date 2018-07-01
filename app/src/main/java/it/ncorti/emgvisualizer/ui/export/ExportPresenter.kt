package it.ncorti.emgvisualizer.ui.export

import it.ncorti.emgvisualizer.dagger.DeviceManager
import it.ncorti.emgvisualizer.MyoApplication
import javax.inject.Inject

class ExportPresenter(val view: ExportContract.View) : ExportContract.Presenter {

    @Inject
    lateinit var deviceManager: DeviceManager

    init {
        MyoApplication.applicationComponent.inject(this)
    }

    override fun start() {

    }

    override fun stop() {

    }
}