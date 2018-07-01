package it.ncorti.emgvisualizer.dagger

import com.ncorti.myonnaise.Myonnaise
import dagger.Component
import dagger.android.AndroidInjector
import it.ncorti.emgvisualizer.MyoApplication
import it.ncorti.emgvisualizer.ui.control.ControlDevicePresenter
import it.ncorti.emgvisualizer.ui.export.ExportPresenter
import it.ncorti.emgvisualizer.ui.graph.GraphPresenter
import it.ncorti.emgvisualizer.ui.scan.ScanDevicePresenter
import javax.inject.Singleton

@Singleton
@Component(modules = [MyonnaiseModule::class, DeviceManagerModule::class])
interface ApplicationComponent : AndroidInjector<MyoApplication> {

    fun provideMyonnaise() : Myonnaise

    fun provideDeviceManager() : DeviceManager

    fun inject(presenter: ScanDevicePresenter)

    fun inject(presenter: ControlDevicePresenter)

    fun inject(presenter: GraphPresenter)

    fun inject(presenter: ExportPresenter)
}

