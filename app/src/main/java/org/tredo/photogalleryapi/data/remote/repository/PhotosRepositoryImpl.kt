package org.tredo.photogalleryapi.data.remote.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.tredo.photogalleryapi.data.base.makePagingRequest
import org.tredo.photogalleryapi.data.paging.PhotosPagingSource
import org.tredo.photogalleryapi.data.remote.service.PhotosApiService
import org.tredo.photogalleryapi.domain.models.PhotoModel
import org.tredo.photogalleryapi.domain.repository.PhotosRepository
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(
    private val photosApiService: PhotosApiService
) : PhotosRepository {

    override fun fetchPhotos(
        isNew: Boolean,
        isPopular: Boolean
    ): Flow<PagingData<PhotoModel>> {
        return makePagingRequest(
            initialLoadSize = 16,
            prefetchDistance = 5,
            pageSize = 16,
            pagingSource = PhotosPagingSource(isNew, isPopular, photosApiService)
        )
    }
}