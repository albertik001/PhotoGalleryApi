package org.tredo.photogalleryapi.presentation.ui.fragments.detail

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import org.tredo.photogalleryapi.R
import org.tredo.photogalleryapi.databinding.FragmentPhotoDetailBinding
import org.tredo.photogalleryapi.presentation.ui.base.BaseFragment
import org.tredo.photogalleryapi.presentation.ui.extensions.loadImageWithPlaceHolder

class PhotoDetailFragment :
    BaseFragment<FragmentPhotoDetailBinding>(R.layout.fragment_photo_detail) {

    override val binding by viewBinding(FragmentPhotoDetailBinding::bind)
    private val args by navArgs<PhotoDetailFragmentArgs>()

    override fun initialize() {
        setDisplayInfo()
    }

    private fun setDisplayInfo() {
        binding.apply {
            imPhoto.loadImageWithPlaceHolder(args.photo, R.drawable.ic_image_placeholder)
            tvTitle.text = args.title
            tvDesc.text = if (args.desc == "") getString(R.string.not_desc) else args.desc
        }
    }

    override fun constructListeners() {
        initBtnBack()
    }

    private fun initBtnBack() {
        binding.imBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}