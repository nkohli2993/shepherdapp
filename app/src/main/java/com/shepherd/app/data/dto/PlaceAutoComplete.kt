package com.shepherd.app.data.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 06/06/22
 */
@Parcelize
data class PlaceAutoComplete(val placeId: String, val address: String, val area: String):Parcelable