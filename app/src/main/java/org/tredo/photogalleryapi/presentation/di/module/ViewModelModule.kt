package org.tredo.photogalleryapi.presentation.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.tredo.photogalleryapi.presentation.di.viewmodelFactory.ViewModelFactory
import org.tredo.photogalleryapi.presentation.ui.fragments.newPhotos.NewPhotosViewModel
import org.tredo.photogalleryapi.presentation.ui.fragments.popularPhotos.PopularPhotosViewModel
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(NewPhotosViewModel::class)
    abstract fun bindNewPhotosViewModel(myViewModel: NewPhotosViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PopularPhotosViewModel::class)
    abstract fun bindPopularPhotosViewModel(myViewModel: PopularPhotosViewModel): ViewModel
}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

