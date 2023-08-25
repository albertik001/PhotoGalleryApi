package org.tredo.photogalleryapi.presentation.ui.fragments.newPhotos

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.tredo.photogalleryapi.domain.usecases.FetchPhotosUseCase
import org.tredo.photogalleryapi.presentation.models.PhotoUI
import org.tredo.photogalleryapi.presentation.models.toUI
import org.tredo.photogalleryapi.presentation.ui.base.BaseViewModel
import javax.inject.Inject

class NewPhotosViewModel @Inject constructor(private val fetchPhotosUseCase: FetchPhotosUseCase) :
    BaseViewModel() {

    private val _photosState = MutableStateFlow<PagingData<PhotoUI>>(PagingData.empty())
    val photoState = _photosState.asStateFlow()

    fun fetchPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchPhotosUseCase(
                isNew = true,
                isPopular = false
            ).gatherPagingRequest(_photosState) {
                it.toUI()
            }
        }
    }
}