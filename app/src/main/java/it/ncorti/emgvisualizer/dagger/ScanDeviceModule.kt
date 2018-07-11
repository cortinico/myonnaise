package it.ncorti.emgvisualizer.dagger

import com.ncorti.myonnaise.Myonnaise
import dagger.Module
import dagger.Provides
import it.ncorti.emgvisualizer.ui.scan.ScanDeviceContract
import it.ncorti.emgvisualizer.ui.scan.ScanDevicePresenter

@Module
class ScanDeviceModule {

    @Provides
    fun provideScanDevicePresenter(
            scanDeviceView: ScanDeviceContract.View,
            myonnaise: Myonnaise,
            deviceManager: DeviceManager
    ): ScanDevicePresenter {
        return ScanDevicePresenter(scanDeviceView, myonnaise, deviceManager)
    }

}
