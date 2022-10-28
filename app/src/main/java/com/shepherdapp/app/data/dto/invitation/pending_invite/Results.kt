package com.shepherdapp.app.data.dto.invitation.pending_invite

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 28/10/22
 */
data class Results(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("care_roles") var careRoles: CareRoles? = CareRoles()

)
