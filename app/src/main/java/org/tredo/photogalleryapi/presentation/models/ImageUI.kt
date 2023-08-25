package org.tredo.photogalleryapi.presentation.models

import org.tredo.photogalleryapi.domain.models.ImageModel


data class ImageUI(
    val id: Int,
    val name: String
)

fun ImageModel.toUI() = ImageUI(id, name)