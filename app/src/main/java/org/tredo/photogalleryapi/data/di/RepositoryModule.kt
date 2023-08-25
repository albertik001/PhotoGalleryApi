package org.tredo.photogalleryapi.data.di

import dagger.Binds
import dagger.Module
import org.tredo.photogalleryapi.data.remote.repository.PhotosRepositoryImpl
import org.tredo.photogalleryapi.domain.repository.PhotosRepository

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindsPhotosRepository(photosRepository: PhotosRepositoryImpl): PhotosRepository
}