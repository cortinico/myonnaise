package it.ncorti.emgvisualizer.dagger

import dagger.Module
import dagger.Provides
import it.ncorti.emgvisualizer.ui.export.ExportContract
import it.ncorti.emgvisualizer.ui.export.ExportPresenter

@Module
class ExportModule {

    @Provides
    fun provideExportPresenter(
            exportView: ExportContract.View,
            deviceManager: DeviceManager
    ): ExportPresenter {
        return ExportPresenter(exportView, deviceManager)
    }

}
