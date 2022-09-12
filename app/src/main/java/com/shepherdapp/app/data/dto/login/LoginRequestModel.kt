package com.shepherdapp.app.data.dto.login

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Sumit Kumar
 */
@Parcelize
data class LoginRequestModel(val email: String, val password: String):Parcelable
