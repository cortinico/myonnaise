package it.ncorti.emgvisualizer.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.ncorti.emgvisualizer.ui.MainActivity
import it.ncorti.emgvisualizer.ui.control.ControlDeviceFragment
import it.ncorti.emgvisualizer.ui.export.ExportFragment
import it.ncorti.emgvisualizer.ui.graph.GraphFragment
import it.ncorti.emgvisualizer.ui.scan.ScanDeviceFragment

@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ScanDeviceViewModule::class, ScanDeviceModule::class])
    abstract fun bindScanDeviceFragment(): ScanDeviceFragment

    @ContributesAndroidInjector(modules = [ControlDeviceViewModule::class, ControlDeviceModule::class])
    abstract fun bindControlDeviceFragment(): ControlDeviceFragment

    @ContributesAndroidInjector(modules = [GraphViewModule::class, GraphModule::class])
    abstract fun bindGraphFragment(): GraphFragment

    @ContributesAndroidInjector(modules = [ExportViewModule::class, ExportModule::class])
    abstract fun bindExportFragment(): ExportFragment
}
