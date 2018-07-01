package it.ncorti.emgvisualizer.dagger

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class DeviceManagerModule {

    @Provides
    @Singleton
    fun provideDeviceManager(): DeviceManager {
        return DeviceManager()
    }

}
