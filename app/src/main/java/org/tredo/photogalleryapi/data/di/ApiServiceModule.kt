package org.tredo.photogalleryapi.data.di

import dagger.Module
import dagger.Provides
import org.tredo.photogalleryapi.data.remote.service.PhotosApiService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiServiceModule {

    @Provides
    @Singleton
    fun providePhotoApiService(retrofit: Retrofit): PhotosApiService =
        retrofit.create(PhotosApiService::class.java)

}