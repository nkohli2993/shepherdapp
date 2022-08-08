package com.shepherd.app.data.dto.med_list.loved_one_med_list

/**
 * Created by Deepak Rattan on 08/08/22
 */
data class MedListReminder(
    var id: Int? = null,
    var assignedBy: String? = null,
    var assignedTo: String? = null,
    var loveUserId: String? = null,
    var dosageId: Int? = null,
    var medlistId: Int? = null,
    var frequency: String? = null,
    var days: String? = null,
    var time: Time? = null,
    var note: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var deletedAt: String? = null,
    var medlist: Medlist? = Medlist()
)
