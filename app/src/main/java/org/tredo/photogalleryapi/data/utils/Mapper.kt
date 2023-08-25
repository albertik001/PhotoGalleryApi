package org.tredo.photogalleryapi.data.utils

/**
 * `Typealias` `Mapper` используется для маппинга из типа `T` в тип `R`.
 *
 * @see [Kotlin Type aliases](https://kotlinlang.org/docs/type-aliases.html)
 */
typealias Mapper<T, R> = (T) -> R

/**
 * Функция-расширение `fromToList`, маппит List<T> в List<R>
 * @param input Входной список типа `List<T>`.
 * @return Список типа `List<R>`, полученный с помощью [Mapper].
 */
fun <T, R> Mapper<T, R>.fromToList(input: List<T>?): List<R> {
    return input?.map { invoke(it) } ?: emptyList()
}