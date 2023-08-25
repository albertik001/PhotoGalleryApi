package org.tredo.photogalleryapi.data.dto


import com.google.gson.annotations.SerializedName
import org.tredo.photogalleryapi.data.utils.DataMapper
import org.tredo.photogalleryapi.domain.models.ImageModel

data class ImageDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
) : DataMapper<ImageModel> {
    override fun toDomain() = ImageModel(id, name)
}
