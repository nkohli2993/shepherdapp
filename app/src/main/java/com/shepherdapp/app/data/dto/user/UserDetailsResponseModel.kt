package com.shepherdapp.app.data.dto.user

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 09/06/22
 */
data class UserDetailsResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
