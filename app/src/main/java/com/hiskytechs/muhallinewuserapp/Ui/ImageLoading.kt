package com.hiskytechs.muhallinewuserapp.Ui

import android.widget.ImageView
import coil.load
import com.hiskytechs.muhallinewuserapp.R

fun ImageView.loadMarketplaceImage(imageUrl: String?) {
    val source = imageUrl?.takeIf { it.isNotBlank() } ?: R.drawable.ic_image_24
    load(source) {
        crossfade(false)
        placeholder(R.drawable.ic_image_24)
        error(R.drawable.ic_image_24)
    }
}
