package com.shepherd.app.data.dto.invitation

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 05/07/22
 */
data class InvitationsResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
