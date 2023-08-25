package org.tredo.photogalleryapi.data.base

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.tredo.photogalleryapi.data.utils.DataMapper
import org.tredo.photogalleryapi.data.utils.Mapper
import org.tredo.photogalleryapi.data.utils.fromJson
import org.tredo.photogalleryapi.domain.either.Either
import org.tredo.photogalleryapi.presentation.ui.state.NetworkError
import retrofit2.Response
import java.io.InterruptedIOException

/**
 * Do network request with [DataMapper.mapToDomain]
 *
 * @receiver [doNetworkRequest]
 */
internal fun <T : DataMapper<S>, S> makeNetworkRequestWithMapping(
    shouldUseAuth: Boolean = false,
    request: suspend () -> Response<T>,
) = makeNetworkRequest(request, shouldUseAuth) { body ->
    Either.Right(body.toDomain())
}

/**
 * Do network request without mapping for primitive types
 *
 * @receiver [doNetworkRequest]
 */
internal fun <T> makeNetworkRequestWithoutMapping(
    shouldUseAuth: Boolean = false,
    request: suspend () -> Response<T>,
) = makeNetworkRequest(request, shouldUseAuth) { body ->
    Either.Right(body)
}

internal fun <Dto, Domain> makeNetworkRequestWithMapping(
    shouldUseAuth: Boolean = false,
    mapper: Mapper<Dto, Domain>,
    request: suspend () -> Response<Dto>
) = makeNetworkRequest(request, shouldUseAuth) { body ->
    Either.Right(mapper.invoke(body))
}

/**
 * Do network request for [Response] with [List]
 *
 * @receiver [doNetworkRequest]
 */
internal fun <T : DataMapper<S>, S> makeNetworkRequestToFetchList(
    shouldUseAuth: Boolean = false,
    request: suspend () -> Response<List<T>>
) = makeNetworkRequest(request, shouldUseAuth) { body ->
    Either.Right(body.map { it.toDomain() })
}

internal fun <T, A> makeNetworkRequestWithAnyReturnType(
    request: suspend () -> Response<T>,
    anything: (T) -> A,
    shouldUseAuth: Boolean = false,
) = makeNetworkRequest(request, shouldUseAuth) { body ->
    Either.Right(anything(body))
}

/**
 * Do network request for and return [Unit]
 *
 * @receiver [doNetworkRequest]
 */
internal fun <T> makeNetworkRequestWithUnitReturnType(
    shouldUseAuth: Boolean = false,
    request: suspend () -> Response<T>
) = makeNetworkRequest(request, shouldUseAuth) {
    Either.Right(Unit)
}

internal fun <T : DataMapper<S>, S> makeNetworkRequestToFetchWithPaging(
    shouldUseAuth: Boolean = false,
    request: suspend () -> Response<BasePagingResponse<T>>
) = makeNetworkRequest(request, shouldUseAuth) { body ->
    Either.Right(body.data.map { it.toDomain() })
}

internal fun <ValueDto : DataMapper<Value>, Value : Any> makePagingRequest(
    pagingSource: BasePagingSource<ValueDto, Value>,
    pageSize: Int = 20,
    prefetchDistance: Int = pageSize,
    enablePlaceholders: Boolean = true,
    initialLoadSize: Int = pageSize * 3,
    maxSize: Int = Int.MAX_VALUE,
    jumpThreshold: Int = Int.MIN_VALUE
) =
    Pager(
        config = PagingConfig(
            pageSize,
            prefetchDistance,
            enablePlaceholders,
            initialLoadSize,
            maxSize,
            jumpThreshold
        ),
        pagingSourceFactory = {
            pagingSource
        }
    ).flow

/**
 * Do request to local database with [DataMapper.mapToDomain]
 *
 * @param request high-order funtion for request to database
 */
internal fun <T : DataMapper<S>, S> makeLocalRequest(
    request: () -> Flow<T>
): Flow<S> = request().map { data -> data.toDomain() }

/**
 * Do request to local database with [DataMapper.mapToDomain] for [List]
 *
 * @param request high-order function for request to database
 */
internal fun <T : DataMapper<S>, S> makeLocalRequestToFetchList(
    request: () -> Flow<List<T>>
): Flow<List<S>> = request().map { list -> list.map { data -> data.toDomain() } }

private fun <T, S> makeNetworkRequest(
    request: suspend () -> Response<T>,
    shouldUseAuth: Boolean = false,
    successful: (T) -> Either.Right<S>
) = flow {
    request().let {
        if (!shouldUseAuth) {
            when {
                it.isSuccessful && it.body() != null -> {
                    emit(successful.invoke(it.body()!!))
                }

                !it.isSuccessful && it.code() == 422 -> {
                    it.errorBody()
                    emit(Either.Left(NetworkError.ApiInputs(it.errorBody().toApiError())))
                }

                else -> {
                    emit(Either.Left(NetworkError.Api(it.errorBody().toApiError())))
                }
            }
        } else {
            when {
                it.isSuccessful && it.body() != null -> {
                    emit(successful.invoke(it.body()!!))
                }

                !it.isSuccessful && it.code() == 422 -> {
                    it.errorBody()
                    emit(Either.Left(NetworkError.ApiInputs(it.errorBody().toApiError())))
                }
            }
        }
    }
}.flowOn(Dispatchers.IO).catch { exception ->
    when (exception) {
        is InterruptedIOException -> {
            emit(Either.Left(NetworkError.Timeout))
        }

        else -> {
            val message = exception.localizedMessage ?: "Error Occurred!"
            emit(Either.Left(NetworkError.Unexpected(message)))
        }
    }
}

internal fun <T> makeNetworkRequestWithoutNetworkError(
    gatherIfSucceed: ((T) -> Unit)? = null,
    request: suspend () -> T
) =
    flow<Either<String, T>> {
        request().also {
            gatherIfSucceed?.invoke(it)
            emit(Either.Right(value = it))
        }
    }.flowOn(Dispatchers.IO).catch { exception ->
        emit(Either.Left(value = exception.localizedMessage ?: "Error Occurred!"))
    }

private inline fun <reified T> ResponseBody?.toApiError(): T? {
    return this?.string()?.let { fromJson<T>(it) }
}

/**
 * Get non-nullable body from network request
 *
 * &nbsp
 *
 * ## How to use:
 * ```
 * override fun fetchFoo() = doNetworkRequestWithMapping {
 *     service.fetchFoo().onSuccess { replenishBusinessSubscriptionDto ->
 *         make something with replenishBusinessSubscriptionDto
 *     }
 * }
 * ```
 *
 * @see Response.body
 * @see let
 */
internal inline fun <T : Response<S>, S> T.onSuccess(block: (S) -> Unit): T {
    this.body()?.let(block)
    return this
}

fun createMultiPartBodyBuilder(type: MediaType): MultipartBody.Builder {
    return MultipartBody.Builder().setType(type)
}

fun Any?.checkForNullabilityAndAddFormDataPart(
    builder: MultipartBody.Builder,
    formDataPartName: String
) {
    when (this is ByteArray?) {
        true -> this?.let {
            builder.addFormDataPart(
                name = "photo",
                filename = "photo.png",
                body = it.toRequestBody("image/png".toMediaTypeOrNull(), 0, it.size)
            )
        }

        false ->
            this?.let { builder.addFormDataPart(formDataPartName, this.toString()) }
    }
}

internal fun <T> List<DataMapper<T>>.toDomain(): List<T> {
    return map { it.toDomain() }
}