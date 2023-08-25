package org.tredo.photogalleryapi.presentation.ui.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

const val URL_FOR_MEDIA = "https://gallery.prod1.webant.ru/media/"

fun ImageView.loadImageWithPlaceHolderAndProgressBar(
    url: String,
    placeHolder: Int,
    progressBar: ProgressBar
) {
    progressBar.visibility = View.VISIBLE

    Glide.with(this)
        .load(URL_FOR_MEDIA + url)
        .placeholder(placeHolder)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false // Позволяем Glide обрабатывать ошибку далее
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false
            }
        })
        .into(this)
}

fun ImageView.loadImageWithPlaceHolder(
    url: String,
    placeHolder: Int
) {

    Glide.with(this)
        .load(URL_FOR_MEDIA + url)
        .placeholder(placeHolder)
        .into(this)
}

