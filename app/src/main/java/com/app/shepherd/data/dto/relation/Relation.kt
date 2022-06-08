package com.app.shepherd.data.dto.relation

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/06/22
 */

data class Relation (
    @SerializedName("id"          ) var id          : Int?     = null,
    @SerializedName("name"        ) var name        : String?  = null,
    @SerializedName("slug"        ) var slug        : String?  = null,
    @SerializedName("description" ) var description : String?  = null,
    @SerializedName("is_active"   ) var isActive    : Boolean? = null,
    @SerializedName("created_at"  ) var createdAt   : String?  = null,
    @SerializedName("updated_at"  ) var updatedAt   : String?  = null,
    @SerializedName("deleted_at"  ) var deletedAt   : String?  = null

)
