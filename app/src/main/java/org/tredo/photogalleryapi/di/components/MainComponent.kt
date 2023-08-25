package org.tredo.photogalleryapi.di.components

import dagger.Component
import org.tredo.photogalleryapi.app.MainApplication
import org.tredo.photogalleryapi.data.di.MainModule
import org.tredo.photogalleryapi.presentation.ui.fragments.newPhotos.NewPhotosFragment
import org.tredo.photogalleryapi.presentation.ui.fragments.popularPhotos.PopularPhotosFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class])
interface MainComponent {

    fun inject(app: MainApplication)
    fun inject(newPhotosFragment: NewPhotosFragment)
    fun inject(popularPhotosFragment: PopularPhotosFragment)

}