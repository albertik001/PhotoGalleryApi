package org.tredo.photogalleryapi.data.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.tredo.photogalleryapi.data.utils.DataMapper
import retrofit2.HttpException
import java.io.IOException

private const val BASE_STARTING_PAGE_INDEX = 1

abstract class BasePagingSource<ValueDto : DataMapper<Value>, Value : Any>(
    private val request: suspend (page: Int, limit: Int) -> BasePagingResponse<ValueDto>,
) : PagingSource<Int, Value>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        val currentPage = params.key ?: BASE_STARTING_PAGE_INDEX
        return try {
            val response = request(currentPage, params.loadSize)

            val nextPage = if (currentPage < response.countOfPages) {
                currentPage + 1
            } else {
                null
            }

            LoadResult.Page(
                data = response.data.map { it.toDomain() },
                prevKey = if (currentPage == BASE_STARTING_PAGE_INDEX) null else currentPage - 1,
                nextKey = nextPage
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
        return null
    }
}
