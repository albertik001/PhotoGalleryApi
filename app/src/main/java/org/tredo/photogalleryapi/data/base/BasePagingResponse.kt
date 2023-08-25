package org.tredo.photogalleryapi.data.base

data class BasePagingResponse<T>(
    val totalItems: Int,
    val itemsPerPage: Int,
    val countOfPages: Int,
    val data: List<T>
)