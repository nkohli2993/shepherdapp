package com.shepherd.app.ui.component.myMedList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherd.app.databinding.AdapterSelectedDayMedicineBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.ClickType
import com.shepherd.app.view_model.MedListViewModel


class SelectedDayMedicineAdapter(
    private val viewModel: MedListViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<SelectedDayMedicineAdapter.SelectedDayMedicineViewHolder>() {
    lateinit var binding: AdapterSelectedDayMedicineBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
             viewModel.openMedDetail(itemData[0] as String)
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
        return 4
    }

    override fun onBindViewHolder(holder: SelectedDayMedicineViewHolder, position: Int) {
//        holder.bind(requestList[position], onItemClickListener)
        holder.bind("", onItemClickListener)
    }


   inner class SelectedDayMedicineViewHolder(private val itemBinding: AdapterSelectedDayMedicineBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(dashboard: String, recyclerItemListener: RecyclerItemListener) {
            // itemBinding.data = dashboard
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    dashboard
                )
            }

            itemBinding.imgMore.setOnClickListener {
                openEditOption(absoluteAdapterPosition,itemBinding.imgMore,context,recyclerItemListener)
            }
        }
       private fun openEditOption(
           position: Int,
           optionsImg: AppCompatImageView,
           context: Context,
           recyclerItemListener: RecyclerItemListener) {

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

    fun addData(dashboard: MutableList<String>) {
        this.requestList.clear()
        this.requestList.addAll(dashboard)
        notifyDataSetChanged()
    }

}