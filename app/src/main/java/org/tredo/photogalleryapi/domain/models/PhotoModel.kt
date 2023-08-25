package org.tredo.photogalleryapi.domain.models


data class PhotoModel(
    val id: Int,
    val name: String,
    val dateCreate: String,
    val description: String? = null,
    val new: Boolean,
    val popular: Boolean,
    val imageModel: ImageModel,
)