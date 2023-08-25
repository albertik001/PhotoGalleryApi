package org.tredo.photogalleryapi.presentation.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.tredo.photogalleryapi.domain.either.Either
import org.tredo.photogalleryapi.presentation.ui.state.NetworkError
import org.tredo.photogalleryapi.presentation.ui.state.UIState

abstract class BaseViewModel : ViewModel() {

    protected fun <T> mutableUiStateFlow() = MutableStateFlow<UIState<T>>(UIState.Idle())

    protected fun <T> mutableUiSharedFlow(extraBufferCapacity: Int = 12) =
        MutableSharedFlow<UIState<T>>(extraBufferCapacity = extraBufferCapacity)

    protected fun <T> MutableStateFlow<UIState<T>>.reset() {
        value = UIState.Idle()
    }

    protected fun <T> MutableSharedFlow<UIState<T>>.reset() {
        tryEmit(UIState.Idle())
    }

    protected fun <T> Flow<Either<NetworkError, T>>.gatherNetworkRequest(
        state: MutableStateFlow<UIState<T>> = mutableUiStateFlow(),
        shouldResetStateOnCompletion: Boolean = false,
    ) = gatherRequest(state, shouldResetStateOnCompletion) {
        UIState.Success(it)
    }

    protected fun <T, S> Flow<Either<NetworkError, T>>.gatherNetworkRequest(
        state: MutableStateFlow<UIState<S>>,
        shouldResetStateOnCompletion: Boolean = false,
        mapToUI: (T) -> S,
    ) = gatherRequest(state, shouldResetStateOnCompletion) {
        UIState.Success(mapToUI(it))
    }

    private fun <T, S> Flow<Either<NetworkError, T>>.gatherRequest(
        state: MutableStateFlow<UIState<S>>,
        shouldResetStateOnCompletion: Boolean = false,
        successful: (T) -> UIState.Success<S>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            state.value = UIState.Loading()
            this@gatherRequest.collect {
                when (it) {
                    is Either.Left -> {
                        state.value = UIState.Error(it.value)
                    }

                    is Either.Right -> {
                        state.value = successful(it.value)
                    }
                }
            }
        }.invokeOnCompletion {
            if (shouldResetStateOnCompletion)
                state.reset()
        }
    }

    private fun <T, S> Flow<Either<NetworkError, T>>.gatherRequest(
        state: MutableSharedFlow<UIState<S>>,
        shouldResetStateOnCompletion: Boolean = false,
        successful: (T) -> UIState.Success<S>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            state.tryEmit(UIState.Loading())
            this@gatherRequest.collect {
                when (it) {
                    is Either.Left -> {
                        state.tryEmit(UIState.Error(it.value))
                    }

                    is Either.Right -> {
                        state.tryEmit(successful(it.value))
                    }
                }
            }
        }.invokeOnCompletion {
            if (shouldResetStateOnCompletion)
                state.reset()
        }
    }

    protected fun <T> Flow<Either<NetworkError, T>>.gatherNetworkRequestForSharedFlow(
        state: MutableSharedFlow<UIState<T>> = mutableUiSharedFlow(),
    ) = gatherRequest(state) {
        UIState.Success(it)
    }

    protected fun <T, S> Flow<Either<NetworkError, T>>.gatherNetworkRequest(
        state: MutableSharedFlow<UIState<S>>,
        mapToUI: (T) -> S,
    ) = gatherRequest(state) {
        UIState.Success(mapToUI(it))
    }

    protected fun <T, S> Flow<T>.gatherLocalRequest(
        mapToUI: (T) -> S,
    ): Flow<S> = map { value: T ->
        mapToUI(value)
    }

    protected fun <Domain, UI> Flow<Domain>.gatherRequest(map: (Domain) -> UI) {
        viewModelScope.launch {
            collectLatest {
                map(it)
            }
        }
    }

    protected fun <T, S> Flow<List<T>>.gatherLocalRequestForList(
        mapToUI: (T) -> S,
    ): Flow<List<S>> = map { value: List<T> ->
        value.map { data: T ->
            mapToUI(data)
        }
    }

    protected fun <T : Any, S : Any> Flow<PagingData<T>>.gatherPagingRequest(
        mapToUI: (T) -> S,
    ): Flow<PagingData<S>> = map { value: PagingData<T> ->
        value.map { data: T ->
            mapToUI(data)
        }
    }.cachedIn(viewModelScope)

    protected fun <T : Any, S : Any> Flow<PagingData<T>>.gatherPagingRequest(
        flowToSave: MutableStateFlow<PagingData<S>>,
        mapToUI: (T) -> S
    ) {
        map { pagingData ->
            pagingData.map(mapToUI)
        }.cachedIn(viewModelScope).onEach {
            flowToSave.value = it
        }.launchIn(viewModelScope)
    }

    protected fun <T> MutableStateFlow<T>.spectateStateFlow(actionWithData: (T) -> Unit) {
        viewModelScope.launch {
            collectLatest {
                actionWithData(it)
            }
        }
    }
}