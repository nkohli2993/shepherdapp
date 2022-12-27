package com.shepherdapp.app.data.dto.delete_account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita kohli 27/12/22
 */
@Parcelize

data class DeleteAccountModel(
    @SerializedName("reason") var reason: String? = null,
) : Parcelable