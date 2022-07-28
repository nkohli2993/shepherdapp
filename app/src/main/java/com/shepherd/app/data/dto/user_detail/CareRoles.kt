package com.shepherd.app.data.dto.user_detail

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 12/07/22
 */
data class CareRoles (

    @SerializedName("id"          ) var id          : Int?     = null,
    @SerializedName("name"        ) var name        : String?  = null,
    @SerializedName("slug"        ) var slug        : String?  = null,
    @SerializedName("description" ) var description : String?  = null,
    @SerializedName("is_active"   ) var isActive    : Boolean? = null,
    @SerializedName("created_at"  ) var createdAt   : String?  = null,
    @SerializedName("updated_at"  ) var updatedAt   : String?  = null,
    @SerializedName("deleted_at"  ) var deletedAt   : String?  = null

)
