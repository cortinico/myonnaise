package it.ncorti.emgvisualizer.dagger

import dagger.Binds
import dagger.Module
import it.ncorti.emgvisualizer.ui.graph.GraphContract
import it.ncorti.emgvisualizer.ui.graph.GraphFragment

@Module
abstract class GraphViewModule {

    @Binds
    abstract fun provideGraphView(exportFragment: GraphFragment): GraphContract.View
}
