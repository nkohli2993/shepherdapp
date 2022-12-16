package com.shepherdapp.app.data.dto.add_vital_stats.update_user_profile_last_sync

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 16/12/22
 */
data class UpdateUserProfileForLastSyncResponseModel(
    @SerializedName("payload") var payload: UserProfile? = UserProfile()
) : BaseResponseModel()
