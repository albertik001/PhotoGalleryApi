package org.tredo.photogalleryapi.presentation.ui.fragments.popularPhotos

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.tredo.photogalleryapi.R
import org.tredo.photogalleryapi.app.MainApplication
import org.tredo.photogalleryapi.databinding.FragmentPopularPhotosBinding
import org.tredo.photogalleryapi.presentation.ui.adapters.PhotosAdapter
import org.tredo.photogalleryapi.presentation.ui.base.BaseFragment
import org.tredo.photogalleryapi.presentation.ui.extensions.directionsSafeNavigation
import javax.inject.Inject

class PopularPhotosFragment :
    BaseFragment<FragmentPopularPhotosBinding>(R.layout.fragment_popular_photos) {

    override val binding by viewBinding(FragmentPopularPhotosBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: PopularPhotosViewModel by viewModels {
        ViewModelProvider(this, viewModelFactory)[PopularPhotosViewModel::class.java]
        viewModelFactory
    }

    private val adapter = PhotosAdapter(this::onItemClick)

    override fun onAttach(context: Context) {
        (context.applicationContext as MainApplication).mainComponent.inject(this)
        super.onAttach(context)
    }

    override fun initialize() {
        viewModel.fetchPhotos()
        initPhotosAdapter()
    }

    private fun initPhotosAdapter() {
        binding.rvNewPhotos.adapter = adapter
    }

    override fun constructListeners() {
        initSwipeRefLayout()
    }

    private fun initSwipeRefLayout() {
        binding.swipeRefLayout.setOnRefreshListener {
            viewModel.fetchPhotos()
        }
    }

    override fun launchObservers() {
        observePhotos()
    }

    private fun observePhotos() {
        viewModel.popularPhotoState.spectateStateFlow {
            if (binding.swipeRefLayout.isRefreshing) binding.swipeRefLayout.isRefreshing = false
            adapter.submitData(it)
        }
    }


    private fun onItemClick(photo: String, title: String, desc: String? = null) {
        findNavController().directionsSafeNavigation(
            PopularPhotosFragmentDirections.actionPopularPhotosFragmentToNewPhotoDetailFragment(
                photo = photo,
                title = title,
                desc = desc ?: getString(R.string.not_desc)
            )
        )
    }
}