package com.shepherdapp.app.data.dto.invitation.delete_pending_invitee

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 20/12/22
 */
data class DeletePendingInviteeByIdResponseModel(
    @SerializedName("payload") var payload: Boolean? = null
) : BaseResponseModel()
