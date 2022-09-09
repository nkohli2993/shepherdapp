package com.shepherd.app.data.dto.menuItem

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Sumit Kumar
 */
data class MenuItemModel(
    val icon: Drawable,
    val title: String
)

