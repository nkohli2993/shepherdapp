package com.shepherd.app.data.dto.login

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/05/22
 */
data class EditResponseModel(
    @SerializedName("payload") var payload: UserProfile? = UserProfile()
) : BaseResponseModel()

