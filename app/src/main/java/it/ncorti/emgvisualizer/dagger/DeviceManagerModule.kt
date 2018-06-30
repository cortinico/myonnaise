package it.ncorti.emgvisualizer.dagger

import android.content.Context
import com.ncorti.myonnaise.Myonnaise
import dagger.Module
import dagger.Provides
import it.ncorti.emgvisualizer.DeviceManager
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class DeviceManagerModule {

    @Provides
    @Singleton
    fun provideDeviceManager(): DeviceManager {
        return DeviceManager()
    }

}
