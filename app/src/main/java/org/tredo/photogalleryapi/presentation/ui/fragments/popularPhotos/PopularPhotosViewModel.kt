package org.tredo.photogalleryapi.presentation.ui.fragments.popularPhotos

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

class PopularPhotosViewModel @Inject constructor(
    private val fetchPhotosUseCase: FetchPhotosUseCase
) : BaseViewModel() {

    private val _popularPhotosState = MutableStateFlow<PagingData<PhotoUI>>(PagingData.empty())
    val popularPhotoState = _popularPhotosState.asStateFlow()

    fun fetchPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchPhotosUseCase(
                isNew = false,
                isPopular = true
            ).gatherPagingRequest(_popularPhotosState) {
                it.toUI()
            }
        }
    }
}