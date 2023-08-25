package org.tredo.photogalleryapi.presentation.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.tredo.photogalleryapi.presentation.ui.state.NetworkError
import org.tredo.photogalleryapi.presentation.ui.state.UIState

abstract class BaseFragment<Binding : ViewBinding>(
    @LayoutRes layoutId: Int
) : Fragment(layoutId) {
    protected abstract val binding: Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        assembleViews()
        constructListeners()
        establishRequest()
        launchObservers()
    }

    protected open fun initialize() {}

    protected open fun assembleViews() {}

    protected open fun constructListeners() {}

    protected open fun establishRequest() {}

    protected open fun launchObservers() {}

    protected fun <T : Any> Flow<PagingData<T>>.spectatePaging(
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        success: suspend (data: PagingData<T>) -> Unit,
    ) {
        customAsyncThread(lifecycleState) {
            collectLatest {
                success(it)
            }
        }
    }

    protected fun <T> StateFlow<UIState<T>>.spectateUiState(
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        state: ((UIState<T>) -> Unit)? = null,
        onIdle: ((UIState<T>) -> Unit)? = null,
        onError: ((error: NetworkError) -> Unit),
        onSuccess: ((data: T) -> Unit),
        onStateSpectated: (() -> Unit)? = null
    ) {
        customAsyncThread(lifecycleState) {
            this@spectateUiState.collect {
                state?.invoke(it)
                when (it) {
                    is UIState.Idle -> {
                        onIdle?.invoke(it)
                    }

                    is UIState.Loading -> {}
                    is UIState.Error -> {
                        onError.invoke(it.error)
                        onStateSpectated?.invoke()
                    }

                    is UIState.Success -> {
                        onSuccess.invoke(it.data)
                        onStateSpectated?.invoke()
                    }
                }
            }
        }
    }

    protected fun <T> StateFlow<T>.spectateStateFlow(
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        actionWhenCollected: suspend (T) -> Unit
    ) {
        customAsyncThread(lifecycleState) {
            this.collectLatest {
                actionWhenCollected(it)
            }
        }
    }

    protected fun <T : Any, VH : RecyclerView.ViewHolder> PagingDataAdapter<T, VH>.submitDataAsync(
        pagingData: PagingData<T>,
        actionWhenSubmitted: (() -> Unit)? = null
    ) {
        customAsyncThread {
            submitData(pagingData)
            actionWhenSubmitted?.invoke()
        }
    }

    protected fun <T : Any, VH : RecyclerView.ViewHolder> PagingDataAdapter<T, VH>.submitDataAsync(
        list: List<T>,
        actionWhenSubmitted: (() -> Unit)? = null
    ) {
        customAsyncThread {
            submitData(PagingData.from(list))
            actionWhenSubmitted?.invoke()
        }
    }

    protected fun <T> StateFlow<UIState<T>>.spectateUiState(
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        success: ((data: T) -> Unit)? = null,
        loading: ((data: UIState.Loading<T>) -> Unit)? = null,
        error: ((error: String) -> Unit)? = null,
        idle: ((idle: UIState.Idle<T>) -> Unit)? = null,
        gatherIfSucceed: ((state: UIState<T>) -> Unit)? = null,
    ) {
        customAsyncThread(lifecycleState) {
            collect {
                gatherIfSucceed?.invoke(it)
                when (it) {
                    is UIState.Idle -> {
                        idle?.invoke(it)
                    }

                    is UIState.Loading -> {
                        loading?.invoke(it)
                    }

                    is UIState.Error ->
                        when (it.error) {
                            is NetworkError.Api -> {
                                error?.invoke(it.error.error.toString())
                            }

                            is NetworkError.ApiInputs -> {
                                error?.invoke(it.error.error.toString())
                            }

                            is NetworkError.Timeout -> {
                                error?.invoke("Timeout")
                            }

                            is NetworkError.Unexpected -> {
                                error?.invoke(it.error.error)
                            }

                        }

                    is UIState.Success -> {
                        success?.invoke(it.data)
                    }
                }
            }
        }
    }

    protected fun NetworkError.setupApiErrors(vararg inputs: TextInputLayout) = when (this) {
        is NetworkError.Unexpected -> {

        }

        is NetworkError.Api -> {

        }

        is NetworkError.ApiInputs -> {
            for (input in inputs) {
                error?.get(input.tag).also { error ->
                    if (error == null) {
                        input.isErrorEnabled = false
                    } else {
                        input.error = error.joinToString()
                        this.error?.remove(input.tag)
                    }
                }
            }
        }

        is NetworkError.Timeout -> {

        }

    }

    protected fun <T> UIState<T>.assembleViewVisibility(
        group: ConstraintLayout,
        loader: CircularProgressIndicator,
        navigationSucceed: Boolean = false,
    ) {
        fun displayLoader(isDisplayed: Boolean) {
            group.isVisible = !isDisplayed
            loader.isVisible = isDisplayed
        }
        when (this) {
            is UIState.Idle -> {

            }

            is UIState.Loading -> {
                displayLoader(true)
            }

            is UIState.Error -> {
                displayLoader(false)
            }

            is UIState.Success -> {
                if (navigationSucceed) {
                    displayLoader(true)
                } else {
                    displayLoader(false)
                }
            }
        }
    }

    protected fun <T> UIState<T>.assembleViewVisibility(
        group: Group,
        loader: CircularProgressIndicator,
        navigationSucceed: Boolean = false,
    ) {
        fun displayLoader(isDisplayed: Boolean) {
            group.isVisible = !isDisplayed
            loader.isVisible = isDisplayed
        }
        when (this) {
            is UIState.Idle -> {

            }

            is UIState.Loading -> {
                displayLoader(true)
            }

            is UIState.Error -> {
                displayLoader(false)
            }

            is UIState.Success -> {
                if (navigationSucceed) {
                    displayLoader(true)
                } else {
                    displayLoader(false)
                }
            }
        }
    }

    fun customAsyncThread(
        lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
        gather: suspend (coroutineScope: CoroutineScope) -> Unit,
    ): Job {
        return viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(lifecycleState) {
                gather(this)
            }
        }
    }

    fun <T> createAsyncForList(
        list: List<T>,
        actionWithAsync: suspend (element: T) -> Unit,
        actionWhenEverythingIsDone: (data: Any) -> Unit
    ) {
        customAsyncThread { coroutineScope ->
            list.map { element ->
                coroutineScope.async(Dispatchers.IO) {
                    actionWithAsync(element)
                }
            }.awaitAll().also {
                actionWhenEverythingIsDone(it)
            }
        }
    }
}