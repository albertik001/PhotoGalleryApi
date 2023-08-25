package org.tredo.photogalleryapi.data.dto


import com.google.gson.annotations.SerializedName
import org.tredo.photogalleryapi.data.utils.DataMapper
import org.tredo.photogalleryapi.domain.models.PhotosModel

data class PhotosDto(
    @SerializedName("totalItems")
    val totalItems: Int,
    @SerializedName("itemsPerPage")
    val itemsPerPage: Int,
    @SerializedName("countOfPages")
    val countOfPages: Int,
    @SerializedName("data")
    val `data`: List<PhotoDto>
) : DataMapper<PhotosModel> {
    override fun toDomain() = PhotosModel(data = data.map { it.toDomain() })
}