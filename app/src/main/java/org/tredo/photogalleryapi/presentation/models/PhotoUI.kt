package org.tredo.photogalleryapi.presentation.models

import org.tredo.photogalleryapi.domain.models.PhotoModel


data class PhotoUI(
    val id: Int,
    val name: String,
    val dateCreate: String,
    val description: String? = null,
    val new: Boolean,
    val popular: Boolean,
    val imageUI: ImageUI,
)

fun PhotoModel.toUI() =
    PhotoUI(id, name, dateCreate, description, new, popular, imageModel.toUI())