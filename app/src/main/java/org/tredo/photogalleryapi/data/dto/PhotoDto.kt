package org.tredo.photogalleryapi.data.dto


import com.google.gson.annotations.SerializedName
import org.tredo.photogalleryapi.data.utils.DataMapper
import org.tredo.photogalleryapi.domain.models.PhotoModel

data class PhotoDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("dateCreate")
    val dateCreate: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("new")
    val new: Boolean,
    @SerializedName("popular")
    val popular: Boolean,
    @SerializedName("image")
    val imageDto: ImageDto,
    @SerializedName("user")
    val user: String
) : DataMapper<PhotoModel> {
    override fun toDomain() =
        PhotoModel(id, name, dateCreate, description, new, popular, imageDto.toDomain())
}