package com.shepherd.app.data.dto.dashboard

import android.graphics.drawable.Drawable

/**
 * Created by Sumit Kumar
 */
data class DashboardModel(
    val icon: Drawable,
    val title: String,
    val subTitle: String,
    val membersCount: String,
    val showImages:Boolean,
    val showTasks:Boolean,
    val taskCount:String, val description:String, val buttonText:String, val colorCode:Int)
