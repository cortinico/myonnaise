package it.ncorti.emgvisualizer

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import it.ncorti.emgvisualizer.dagger.ApplicationComponent
import it.ncorti.emgvisualizer.dagger.ContextModule
import it.ncorti.emgvisualizer.dagger.DaggerApplicationComponent
import javax.inject.Inject

class MyoApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    companion object {
        @JvmStatic lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent
            .builder()
            .contextModule(ContextModule(applicationContext))
            .build()

        applicationComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}
