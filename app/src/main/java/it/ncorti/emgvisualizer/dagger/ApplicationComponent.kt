package it.ncorti.emgvisualizer.dagger

import com.ncorti.myonnaise.Myo
import com.ncorti.myonnaise.Myonnaise
import dagger.Component
import dagger.android.AndroidInjector
import it.ncorti.emgvisualizer.MyoApplication
import it.ncorti.emgvisualizer.ui.control.ControlDevicePresenter
import it.ncorti.emgvisualizer.ui.export.ExportFragment
import it.ncorti.emgvisualizer.ui.export.ExportPresenter
import it.ncorti.emgvisualizer.ui.graph.GraphPresenter
import it.ncorti.emgvisualizer.ui.scan.ScanDevicePresenter
import javax.inject.Singleton
import dagger.BindsInstance
import dagger.android.support.AndroidSupportInjectionModule


@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ContextModule::class,
    BuildersModule::class,
    MyonnaiseModule::class,
    DeviceManagerModule::class
]) interface ApplicationComponent : AndroidInjector<MyoApplication>