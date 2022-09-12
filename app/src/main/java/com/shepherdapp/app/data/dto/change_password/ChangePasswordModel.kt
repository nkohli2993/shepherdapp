package com.shepherdapp.app.data.dto.change_password

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita kohli 22/08/22
 */
@Parcelize

data class ChangePasswordModel(
    @SerializedName("old_password") var oldPassword: String? = null,
    @SerializedName("new_password") var newPassword: String? = null,
    @SerializedName("confirm_password") var confirmPassword: String? = null
) : Parcelable