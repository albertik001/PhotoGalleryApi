package org.tredo.photogalleryapi.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.tredo.photogalleryapi.domain.models.PhotoModel
import org.tredo.photogalleryapi.domain.models.PhotosModel

interface PhotosRepository {

    fun fetchPhotos(isNew: Boolean, isPopular: Boolean): Flow<PagingData<PhotoModel>>
}