package org.tredo.photogalleryapi.app

import android.app.Application
import org.tredo.photogalleryapi.data.di.MainModule
import org.tredo.photogalleryapi.di.components.DaggerMainComponent
import org.tredo.photogalleryapi.di.components.MainComponent

class MainApplication : Application() {

    val mainComponent: MainComponent by lazy {
        DaggerMainComponent.builder().mainModule(MainModule()).build()
    }

    override fun onCreate() {
        mainComponent.inject(this)
        super.onCreate()
    }
}