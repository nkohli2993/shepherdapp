package com.shepherdapp.app.data.dto.security_code

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/* Created by Nikita kohli on 24/08/22
*/
data class SecurityCodeResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
