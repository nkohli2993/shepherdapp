package com.shepherdapp.app.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.io.File


fun ImageView.loadImage(context: Context, url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}


fun ImageView.loadImageFromUriRoundCorner(context: Context, path: String?) {
    Glide.with(context)
        .load(Uri.fromFile(File(path)))
        .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(10)))
        .into(this)
}