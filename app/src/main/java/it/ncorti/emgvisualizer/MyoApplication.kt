package it.ncorti.emgvisualizer

import android.app.Application
import it.ncorti.emgvisualizer.dagger.ApplicationComponent
import it.ncorti.emgvisualizer.dagger.ContextModule
import it.ncorti.emgvisualizer.dagger.DaggerApplicationComponent

class MyoApplication : Application(){

    companion object {
        @JvmStatic lateinit var applicationComponent : ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent
                .builder()
                .contextModule(ContextModule(applicationContext))
                .build()
    }
}

