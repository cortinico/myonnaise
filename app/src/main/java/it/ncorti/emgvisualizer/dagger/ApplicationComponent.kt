package it.ncorti.emgvisualizer.dagger

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import it.ncorti.emgvisualizer.MyoApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ContextModule::class,
        BuildersModule::class,
        MyonnaiseModule::class,
        DeviceManagerModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<MyoApplication>