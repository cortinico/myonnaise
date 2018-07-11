package it.ncorti.emgvisualizer.dagger

import dagger.Binds
import dagger.Module
import it.ncorti.emgvisualizer.ui.control.ControlDeviceContract
import it.ncorti.emgvisualizer.ui.control.ControlDeviceFragment

@Module
abstract class ControlDeviceViewModule {

    @Binds
    abstract fun provideControlDeviceView(exportFragment: ControlDeviceFragment): ControlDeviceContract.View
}