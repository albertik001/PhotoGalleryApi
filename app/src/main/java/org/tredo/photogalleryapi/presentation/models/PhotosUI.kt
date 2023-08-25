package org.tredo.photogalleryapi.presentation.models

import org.tredo.photogalleryapi.domain.models.PhotosModel


data class PhotosUI(
    val `data`: List<PhotoUI>
)

fun PhotosModel.toUI() = PhotosUI(data.map { it.toUI() })