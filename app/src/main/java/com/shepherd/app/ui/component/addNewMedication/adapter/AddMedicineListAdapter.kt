package com.shepherd.app.ui.component.addNewMedication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.med_list.Medlist
import com.shepherd.app.databinding.AdapterAddMedicineListBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.AddMedicationViewModel

@SuppressLint("NotifyDataSetChanged")
class AddMedicineListAdapter(
    private val viewModel: AddMedicationViewModel,
    var medLists: MutableList<Medlist> = ArrayList()
) :
    RecyclerView.Adapter<AddMedicineListAdapter.AddMedicineListViewHolder>() {
    lateinit var binding: AdapterAddMedicineListBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openScheduleMedication(itemData[0] as Int)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMedicineListViewHolder {
        context = parent.context
        binding =
            AdapterAddMedicineListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddMedicineListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return medLists.size
    }

    override fun onBindViewHolder(holder: AddMedicineListViewHolder, position: Int) {
        holder.bind(medLists[position], onItemClickListener)
    }


    class AddMedicineListViewHolder(private val itemBinding: AdapterAddMedicineListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(medList: Medlist, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = medList
              /* itemBinding.cbReminder.isChecked = false
               if (medList.isSelected) {
                   itemBinding.cbReminder.isChecked = true
               }*/
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    absoluteAdapterPosition
                )
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun addData(medLists: ArrayList<Medlist>, isSearchData: Boolean = false) {
        if (isSearchData) {
            this.medLists = medLists
        } else {
            this.medLists.addAll(medLists)
        }
        notifyDataSetChanged()
    }

    fun setList(medLists: ArrayList<Medlist>) {
        this.medLists = medLists
        notifyDataSetChanged()
    }

    fun clearData() {
        this.medLists.clear()
    }

}