package it.ncorti.emgvisualizer.dagger

import com.ncorti.myonnaise.Myonnaise
import dagger.Module
import dagger.Provides
import it.ncorti.emgvisualizer.ui.control.ControlDeviceContract
import it.ncorti.emgvisualizer.ui.control.ControlDevicePresenter
import it.ncorti.emgvisualizer.ui.export.ExportContract
import it.ncorti.emgvisualizer.ui.export.ExportPresenter

@Module
class ControlDeviceModule {

    @Provides
    fun provideControlDevicePresenter(
            controlDeviceView: ControlDeviceContract.View,
            myonnaise: Myonnaise,
            deviceManager: DeviceManager
    ): ControlDevicePresenter {
        return ControlDevicePresenter(controlDeviceView, myonnaise, deviceManager)
    }

}
