package com.shepherdapp.app.data.dto.add_new_member_care_team
import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/06/22
 */
data class AddNewMemberCareTeamResponseModel(
    @SerializedName("payload") var payload: Payload
) : BaseResponseModel()
