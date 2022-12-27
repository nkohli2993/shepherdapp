package com.shepherdapp.app.data.dto.add_vital_stats.update_user_profile_last_sync

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/12/22
 */
data class UpdateUserProfileForLastSyncRequestModel(
    @SerializedName("settings" ) var settings : Settings? = Settings()
)
