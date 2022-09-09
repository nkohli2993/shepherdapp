package com.shepherd.app.data.dto.med_list.get_medication_detail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 09/08/22
 */

@Parcelize
data class Love_user_id_details (

	@SerializedName("id") val id : Int,
	@SerializedName("uid") val uid : String,
	@SerializedName("email") val email : String,
	@SerializedName("firstname") val firstname : String,
	@SerializedName("lastname") val lastname : String,
	@SerializedName("profile_photo") val profile_photo : String,
	@SerializedName("phone") val phone : String,
	@SerializedName("address") val address : String
):Parcelable