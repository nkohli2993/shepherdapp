package com.shepherdapp.app.data.dto.invitation.pending_invite

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 28/10/22
 */
data class PendingInviteResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
