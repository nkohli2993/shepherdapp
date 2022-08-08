package com.shepherd.app.ui.component.myMedList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.loved_one_med_list.MedListReminder
import com.shepherd.app.databinding.AdapterSelectedDayMedicineBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.MyMedListViewModel


class SelectedDayMedicineAdapter(
    private val viewModel: MyMedListViewModel,
    var payload: MutableList<MedListReminder> = ArrayList()
) :
    RecyclerView.Adapter<SelectedDayMedicineAdapter.SelectedDayMedicineViewHolder>() {
    lateinit var binding: AdapterSelectedDayMedicineBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
//            viewModel.openMedDetail(itemData[0] as Medlist)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedDayMedicineViewHolder {
        context = parent.context
        binding =
            AdapterSelectedDayMedicineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SelectedDayMedicineViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return payload.size
    }

    override fun onBindViewHolder(holder: SelectedDayMedicineViewHolder, position: Int) {
//        holder.bind(requestList[position], onItemClickListener)
        holder.bind(payload[position], onItemClickListener)
    }


    inner class SelectedDayMedicineViewHolder(private val itemBinding: AdapterSelectedDayMedicineBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(medListReminder: MedListReminder?, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = medListReminder
            itemBinding.root.setOnClickListener {
                medListReminder?.let { it1 ->
                    recyclerItemListener.onItemSelected(
                        it1
                    )
                }
            }

            itemBinding.imgMore.setOnClickListener {
                openEditOption(
                    absoluteAdapterPosition,
                    itemBinding.imgMore,
                    context,
                    recyclerItemListener
                )
            }
        }

        private fun openEditOption(
            position: Int,
            optionsImg: AppCompatImageView,
            context: Context,
            recyclerItemListener: RecyclerItemListener
        ) {

            val popup = PopupMenu(context, optionsImg)
            popup.inflate(R.menu.options_menu_medication)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit_medication -> {

                        true
                    }
                    R.id.delete_medication -> {
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(payload: ArrayList<MedListReminder>) {
//        this.requestList.clear()
//        this.requestList.addAll(dashboard)
        this.payload.clear()
        this.payload = payload
        notifyDataSetChanged()
    }

}