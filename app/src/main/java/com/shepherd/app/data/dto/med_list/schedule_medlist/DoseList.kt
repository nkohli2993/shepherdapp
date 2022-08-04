package com.shepherd.app.data.dto.med_list.schedule_medlist

import com.google.gson.annotations.SerializedName


data class DoseList(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("deleted_at")
    var deletedAt: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("is_active")
    var isActive: Boolean = false,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("is_selected")
    var isSelected: Boolean = false,
)