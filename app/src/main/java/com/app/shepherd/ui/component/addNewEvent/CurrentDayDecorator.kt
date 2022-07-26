package com.app.shepherd.ui.component.addNewEvent
import android.R
import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*


/**
 * Created by Navruz on 17.06.2016.
 */
class CurrentDayDecorator(context: Activity?) : DayViewDecorator {
    private val drawable: Drawable?
    var currentDay: CalendarDay = CalendarDay.from(Calendar.getInstance())
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == currentDay
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable!!)
    }

    init {
        drawable = ContextCompat.getDrawable(context!!, R.mipmap.sym_def_app_icon)
    }
}