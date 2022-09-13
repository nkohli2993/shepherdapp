package com.shepherdapp.app.data.dto.med_list.add_med_list

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.med_list.get_medication_detail.Medlist
import com.shepherdapp.app.ui.base.BaseResponseModel


/*
*  Created by Nikita kohli on 13/09/22
*/

data class AddedMedlistResponseModel(
    @SerializedName("payload") var payload: Medlist? = Medlist()
) : BaseResponseModel()
