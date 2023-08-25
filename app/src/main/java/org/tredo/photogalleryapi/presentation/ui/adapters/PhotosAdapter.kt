package org.tredo.photogalleryapi.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.tredo.photogalleryapi.R
import org.tredo.photogalleryapi.databinding.ItemPhotoBinding
import org.tredo.photogalleryapi.presentation.models.PhotoUI
import org.tredo.photogalleryapi.presentation.ui.extensions.loadImageWithPlaceHolderAndProgressBar

class PhotosAdapter(private val onItemClick: (photo: String, title: String, desc: String?) -> Unit) :
    PagingDataAdapter<PhotoUI, PhotosAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: PhotoUI) {

            binding.imPhoto.loadImageWithPlaceHolderAndProgressBar(
                photo.imageUI.name,
                placeHolder = R.drawable.ic_image_placeholder,
                binding.cpi
            )

            binding.root.setOnClickListener {
                onItemClick(photo.imageUI.name, photo.name, photo.description)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoItem = getItem(position)
        if (photoItem != null) {
            holder.bind(photoItem)
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<PhotoUI>() {
        override fun areItemsTheSame(oldItem: PhotoUI, newItem: PhotoUI): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhotoUI, newItem: PhotoUI): Boolean {
            return oldItem == newItem
        }
    }
}
