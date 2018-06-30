package it.ncorti.emgvisualizer.dagger

import android.content.Context
import com.ncorti.myonnaise.Myonnaise
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class MyonnaiseModule {

    @Provides
    @Singleton
    fun provideMyonnaise(context: Context): Myonnaise {
        return Myonnaise(context)
    }

}
