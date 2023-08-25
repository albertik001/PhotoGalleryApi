package org.tredo.photogalleryapi.data.remote.service

import org.tredo.photogalleryapi.data.base.BasePagingResponse
import org.tredo.photogalleryapi.data.dto.PhotoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosApiService {

    @GET("api/photos")
    suspend fun requestFetchPhotosUsingParameters(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("new") isNew: Boolean,
        @Query("popular") isPopular: Boolean
    ): BasePagingResponse<PhotoDto>
}