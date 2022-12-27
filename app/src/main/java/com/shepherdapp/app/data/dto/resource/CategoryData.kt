package com.shepherdapp.app.data.dto.resource
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryData(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,
    @SerializedName("slug")
    var slug: String? = null,
    @SerializedName("created_at")
    var created_at: String? = null,
    @SerializedName("updated_at")
    var updated_at: String? = null,
    @SerializedName("deleted_at")
    var deleted_at: String? = null,
    @SerializedName("is_selected")
    var isSelected :Boolean = true

) : Parcelable