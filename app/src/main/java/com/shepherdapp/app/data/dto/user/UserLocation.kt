package com.shepherdapp.app.data.dto.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 09/09/22
 */
@Parcelize
data class UserLocation(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("formatted_address") var formattedAddress: String? = null,
    @SerializedName("address_line_1") var addressLine1: String? = null,
    @SerializedName("address_line_2") var addressLine2: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("postal_code") var postalCode: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("place_id") var placeId: String? = null,
    @SerializedName("geometry") var geometry: Geometry? = Geometry(),
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
) : Parcelable
