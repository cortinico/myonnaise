package it.ncorti.emgvisualizer.dagger

import dagger.Module
import dagger.Provides
import it.ncorti.emgvisualizer.ui.graph.GraphContract
import it.ncorti.emgvisualizer.ui.graph.GraphPresenter

@Module
class GraphModule {

    @Provides
    fun provideGraphPresenter(
        graphView: GraphContract.View,
        deviceManager: DeviceManager
    ): GraphPresenter {
        return GraphPresenter(graphView, deviceManager)
    }
}
