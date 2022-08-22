package com.shepherd.app.data.dto.change_password

import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli 22/08/22
 */

data class ChangePasswordModel(
    @SerializedName("old_password") var oldPassword: String? = null,
    @SerializedName("new_password") var newPassword: String? = null,
    @SerializedName("confirm_password") var confirmPassword: String? = null
)
