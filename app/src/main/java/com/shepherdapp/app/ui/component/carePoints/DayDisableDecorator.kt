package com.shepherdapp.app.ui.component.carePoints

import android.util.Log
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DayDisableDecorator : DayViewDecorator {
    //    private val drawable: Drawable?
//    var myDay = currentDay
    private val TAG = "DayDisableDecorator"

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val currentDay = CalendarDay.today()
        Log.d(TAG, "shouldDecorate: CurrentDay is ${currentDay}")
        return day.isBefore(currentDay)
    }

    override fun decorate(view: DayViewFacade) {
//        view.setSelectionDrawable(drawable!!)
        view.setDaysDisabled(true)
    }

    init {
        // You can set background for Decorator via drawable here
//        drawable = ContextCompat.getDrawable(context!!, R.drawable.checkbox_off_background)
    }
}