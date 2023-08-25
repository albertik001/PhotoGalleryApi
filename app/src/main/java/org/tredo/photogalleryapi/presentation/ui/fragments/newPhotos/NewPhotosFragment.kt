package org.tredo.photogalleryapi.presentation.ui.fragments.newPhotos

import android.content.Context
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.tredo.photogalleryapi.R
import org.tredo.photogalleryapi.app.MainApplication
import org.tredo.photogalleryapi.databinding.FragmentNewPhotosBinding
import org.tredo.photogalleryapi.presentation.ui.adapters.PhotosAdapter
import org.tredo.photogalleryapi.presentation.ui.base.BaseFragment
import org.tredo.photogalleryapi.presentation.ui.extensions.directionsSafeNavigation
import javax.inject.Inject

class NewPhotosFragment : BaseFragment<FragmentNewPhotosBinding>(R.layout.fragment_new_photos) {

    override val binding by viewBinding(FragmentNewPhotosBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: NewPhotosViewModel by viewModels {
        ViewModelProvider(this, viewModelFactory)[NewPhotosViewModel::class.java]
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
        viewModel.photoState.spectateStateFlow {
            if (binding.swipeRefLayout.isRefreshing) binding.swipeRefLayout.isRefreshing = false
            adapter.submitData(it)
        }
    }

    private fun onItemClick(photo: String, title: String, desc: String? = null) {
        findNavController().directionsSafeNavigation(
            NewPhotosFragmentDirections.actionNewPhotoFragmentToNewPhotoDetailFragment(
                photo = photo,
                title = title,
                desc = desc ?: getString(R.string.not_desc)
            )
        )
    }
}