package com.shepherd.app.data.dto.med_list.get_medication_detail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 09/08/22
 */

@Parcelize
data class Payload (

	@SerializedName("id") val id : Int,
	@SerializedName("assigned_by") val assigned_by : String,
	@SerializedName("assigned_to") val assigned_to : String,
	@SerializedName("love_user_id") val love_user_id : String,
	@SerializedName("dosage_id") val dosage_id : Int,
	@SerializedName("dosage_type_id") val dosage_type_id : Int,
	@SerializedName("medlist_id") val medlist_id : Int,
	@SerializedName("frequency") val frequency : Int,
	@SerializedName("days") val days : String,
	@SerializedName("time") val time : List<Time>,
	@SerializedName("note") val note : String,
	@SerializedName("end_date") val end_date : String? =null,
	@SerializedName("created_at") val created_at : String,
	@SerializedName("updated_at") val updated_at : String,
	@SerializedName("deleted_at") val deleted_at : String,
	@SerializedName("deletedAt") val deletedAt : String,
	@SerializedName("medlist") val medlist : Medlist,
	@SerializedName("dosage") val dosage : Dosage,
	@SerializedName("assigned_by_details") val assigned_by_details : AssignedByDetails,
	@SerializedName("love_user_id_details") val love_user_id_details : Love_user_id_details
):Parcelable