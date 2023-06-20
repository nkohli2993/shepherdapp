package com.shepherdapp.app.ui.component.addNewEvent.adapter

import CommonFunctions
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.MonthModel
import com.shepherdapp.app.databinding.AdapterMonthBinding
import com.shepherdapp.app.utils.extensions.showError
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MonthAdapter(
    val context: Context,
    var monthList: ArrayList<MonthModel> = ArrayList(),
    val onListener: selectedMonth
) :
    RecyclerView.Adapter<MonthAdapter.AddAssigneListViewHolder>() {
    lateinit var binding: AdapterMonthBinding
    private var startDate: Date? = null
    var endDate: Date? = null
    var startDateMonth: Int? = null
    var endDateMonth: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAssigneListViewHolder {
        binding =
            AdapterMonthBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddAssigneListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return monthList.size
    }

    override fun onBindViewHolder(holder: AddAssigneListViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class AddAssigneListViewHolder(private val itemBinding: AdapterMonthBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int) {

            itemBinding.appCompatTextViewMonth.text = monthList[position].monthDate

            if (monthList[position].isSelected) {
                itemBinding.appCompatTextViewMonth.backgroundTintList =
                    AppCompatResources.getColorStateList(context, R.color._399282)
                itemBinding.appCompatTextViewMonth.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorWhite
                    )
                )

            } else {
                itemBinding.appCompatTextViewMonth.backgroundTintList =
                    AppCompatResources.getColorStateList(context, R.color.colorWhite)
                itemBinding.appCompatTextViewMonth.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlack
                    )
                )
            }

            itemBinding.appCompatTextViewMonth.setOnClickListener{
                addDateIntoList(position)
            }
/*
            {

                val selectedDayMonth = if (monthList[position].monthDate.length == 1)
                    "0" + monthList[position].monthDate
                else monthList[position].monthDate

                if (getSelectedStartDateMonth() == getSelectedEndDateMonth()) {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDayMonth.toInt())
                    val selectedDateString = SimpleDateFormat("MM/dd/yyyy").format(calendar.time)
                    val selectedDate = SimpleDateFormat("MM/dd/yyyy").parse(selectedDateString)


                    if (selectedDate?.before(startDate) == true)
                        showError(
                            it.context,
                            context.getString(R.string.selected_date_s_should_lie_in_between_start_end_date)
                        )
                    else if (selectedDate?.after(endDate) == true)
                        showError(
                            it.context,
                            context.getString(R.string.selected_date_s_should_lie_in_between_start_end_date)
                        )
                    else {
                        onListener.onMonthSelected(monthList[position],position)
                        monthList[position].isSelected = !monthList[position].isSelected
                        notifyDataSetChanged()
                    }
                } else if (getSelectedEndDateMonth() - getSelectedStartDateMonth() == 1) {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDayMonth.toInt())

                    val selectedDateString = SimpleDateFormat("MM/dd/yyyy").format(calendar.time)
                    val selectedStartDateString = SimpleDateFormat("MM/dd/yyyy").format(startDate)
                    val selectedEndDateString = SimpleDateFormat("MM/dd/yyyy").format(endDate)

                    val selectedDateOnly =
                        CommonFunctions.formatDate(selectedDateString, "MM/dd/yyyy", "dd")
                    val selectedDateCalendar = Calendar.getInstance()
                    selectedDateCalendar.time = selectedDateOnly
                    val selectedDayInt = selectedDateCalendar.get(Calendar.DAY_OF_MONTH)

                    val selectedStartDateOnly =
                        CommonFunctions.formatDate(selectedStartDateString, "MM/dd/yyyy", "dd")
                    val selectedStartDateCalendar = Calendar.getInstance()
                    selectedStartDateCalendar.time = selectedStartDateOnly
                    val selectedStartDateInt = selectedStartDateCalendar.get(Calendar.DAY_OF_MONTH)


                    val selectedEndDateOnly =
                        CommonFunctions.formatDate(selectedEndDateString, "MM/dd/yyyy", "dd")
                    val selectedEndDateCalendar = Calendar.getInstance()
                    selectedEndDateCalendar.time = selectedEndDateOnly
                    val selectedEndDateInt = selectedEndDateCalendar.get(Calendar.DAY_OF_MONTH)

                    val maxDate: Int
                    val minDate: Int

                    if (selectedStartDateInt > selectedEndDateInt) {
                        maxDate = selectedStartDateInt
                        minDate = selectedEndDateInt
                    } else {
                        minDate = selectedStartDateInt
                        maxDate = selectedEndDateInt
                    }

                    if (selectedDayInt == selectedEndDateInt) {
                        addDateIntoList(position)
                    } else if (CommonFunctions.between(selectedDayInt, minDate, maxDate))
                        showError(
                             it.context,
                            context.getString(R.string.selected_date_s_should_lie_in_between_start_end_date)
                        )
                    else {
                        addDateIntoList(position)

                    }
                } else {
                    addDateIntoList(position)
                }
            }
*/
        }
    }

    private fun addDateIntoList(position: Int) {
        onListener.onMonthSelected(monthList[position],position)
        monthList[position].isSelected = !monthList[position].isSelected
        notifyDataSetChanged()
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    public fun startEndDate(startDate: Date, endDate: Date) {
        this.startDate = startDate
        this.endDate = endDate

    }

    private fun getSelectedStartDateMonth(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = startDate!!
        return calendar.get(Calendar.MONTH);

    }

    private fun getSelectedEndDateMonth(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = endDate!!
        return calendar.get(Calendar.MONTH);

    }

    fun clearSelectedList() {
        monthList.forEach {
            it.isSelected = false
        }

        notifyDataSetChanged()
    }


    interface selectedMonth {
        fun onMonthSelected(monthModel: MonthModel,position: Int)
    }
}