package org.tredo.photogalleryapi.data.di

import dagger.Module
import org.tredo.photogalleryapi.presentation.di.module.ViewModelModule

@Module(includes = [NetworkModule::class, ApiServiceModule::class, RepositoryModule::class, ViewModelModule::class])
class MainModule