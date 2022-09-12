package com.shepherdapp.app.data.dto.login

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/05/22
 */
data class EditResponseModel(
    @SerializedName("payload") var payload: UserProfile? = UserProfile()
) : BaseResponseModel()

