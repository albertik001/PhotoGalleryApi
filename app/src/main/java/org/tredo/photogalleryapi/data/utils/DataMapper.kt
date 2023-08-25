package org.tredo.photogalleryapi.data.utils

interface DataMapper<T> {
    fun toDomain(): T
}