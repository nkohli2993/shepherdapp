package com.shepherd.app.data.dto.settings_pages

import com.shepherd.app.data.dto.settings_pages.Payload
import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/* Created by Nikita kohli on 09/09/22
*/
data class StaticPageResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
