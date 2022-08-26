package com.shepherd.app.data.dto.medical_conditions

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 07/06/22
 */
data class Conditions(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("data") var data: String? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("is_selected") var isSelected: Boolean = false,
//    @SerializedName("is_already_selected") var isAlreadySelected: Boolean? = null,
    @SerializedName("is_already_selected") var isAlreadySelected: Int = 0,
    @SerializedName("add_condition_id") var addConditionId: Int? = null
)
