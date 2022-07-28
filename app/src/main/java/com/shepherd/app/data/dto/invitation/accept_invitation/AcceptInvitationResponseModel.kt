package com.shepherd.app.data.dto.invitation.accept_invitation

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 11/07/22
 */
data class AcceptInvitationResponseModel(
    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()