package com.shepherdapp.app.ui.component.carePoints.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.WeekDataModel
import com.shepherdapp.app.databinding.AdapterWeekdaysBinding
import kotlinx.android.synthetic.main.adapter_weekdays.view.nameTV
import okhttp3.internal.notify

class WeekAdapter (
    var weekList: ArrayList<WeekDataModel> = arrayListOf(),
    val listener :WeekDaySelected
) : RecyclerView.Adapter<WeekAdapter.WeekViewHolder>() {
    lateinit var binding: AdapterWeekdaysBinding
    lateinit var context: Context
    var weekDayNameList: List<String>? = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeekViewHolder {
        context = parent.context
        binding =
            AdapterWeekdaysBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return WeekViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return weekList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val data = weekList[position]
        holder.itemView.nameTV.text = data.name
        holder.itemView.nameTV.setBackgroundResource(R.drawable.button_grey)
        holder.itemView.nameTV.setTextColor(ContextCompat.getColor(context,R.color._192032))

        if(data.isSelected){
            holder.itemView.nameTV.setBackgroundResource(R.drawable.button_green)
            holder.itemView.nameTV.setTextColor(ContextCompat.getColor(context,R.color.colorWhite))
        }

        holder.itemView.rootView.setOnClickListener {
            if (weekDayNameList?.contains(data.name!!)!!) {
                data.isSelected = !data.isSelected
                listener.onDaySelected(weekList)
                notifyDataSetChanged()
            }
        }
    }


    class WeekViewHolder(private val itemBinding: AdapterWeekdaysBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setDayNameList(weekDayNameList: List<String>?) {
        this.weekDayNameList = weekDayNameList
    }
    fun clearSelectedList() {
        weekList.forEach {
            it.isSelected = false
        }

        notifyDataSetChanged()
    }


    interface  WeekDaySelected{
        fun onDaySelected(detail: ArrayList<WeekDataModel>)
    }
}