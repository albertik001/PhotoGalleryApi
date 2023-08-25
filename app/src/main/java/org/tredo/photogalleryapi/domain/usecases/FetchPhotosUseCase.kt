package org.tredo.photogalleryapi.domain.usecases

import org.tredo.photogalleryapi.domain.repository.PhotosRepository
import javax.inject.Inject

class FetchPhotosUseCase @Inject constructor(private val repository: PhotosRepository) {

    operator fun invoke(isNew: Boolean, isPopular: Boolean) =
        repository.fetchPhotos(isNew, isPopular)
}