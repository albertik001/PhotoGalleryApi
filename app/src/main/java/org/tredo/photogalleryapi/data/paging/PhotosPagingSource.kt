package org.tredo.photogalleryapi.data.paging

import org.tredo.photogalleryapi.data.base.BasePagingSource
import org.tredo.photogalleryapi.data.dto.PhotoDto
import org.tredo.photogalleryapi.data.remote.service.PhotosApiService
import org.tredo.photogalleryapi.domain.models.PhotoModel

class PhotosPagingSource(
    private val isNew: Boolean,
    private val isPopular: Boolean,
    private val apiService: PhotosApiService
) : BasePagingSource<PhotoDto, PhotoModel>({ page, limit ->
    apiService.requestFetchPhotosUsingParameters(
        page = page, limit = limit, isNew = isNew, isPopular = isPopular
    )
})