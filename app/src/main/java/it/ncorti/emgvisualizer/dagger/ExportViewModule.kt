package it.ncorti.emgvisualizer.dagger

import dagger.Binds
import dagger.Module
import it.ncorti.emgvisualizer.ui.export.ExportContract
import it.ncorti.emgvisualizer.ui.export.ExportFragment

@Module
abstract class ExportViewModule {

    @Binds
    abstract fun provideExportView(exportFragment: ExportFragment): ExportContract.View
}