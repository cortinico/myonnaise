package it.ncorti.emgvisualizer.dagger

import dagger.Binds
import dagger.Module
import it.ncorti.emgvisualizer.ui.scan.ScanDeviceContract
import it.ncorti.emgvisualizer.ui.scan.ScanDeviceFragment

@Module
abstract class ScanDeviceViewModule {

    @Binds
    abstract fun provideScanDeviceView(scanFragment: ScanDeviceFragment): ScanDeviceContract.View
}
