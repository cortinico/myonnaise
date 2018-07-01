package it.ncorti.emgvisualizer.dagger

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private var context: Context) {

    @Provides
    fun context(): Context {
        return context.applicationContext
    }
}

