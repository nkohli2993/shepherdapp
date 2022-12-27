package com.shepherdapp.app.data.dto.medical_conditions.edit_medical_conditions

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 09/12/22
 */
data class Payload(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("data") var data: String? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("created_by") var createdBy: String? = null,
    @SerializedName("created_by_id") var createdById: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
)
