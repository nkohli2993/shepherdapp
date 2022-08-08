package com.shepherd.app.ui.component.myMedList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Medlist
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Payload
import com.shepherd.app.databinding.AdapterMyMedicationsListBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.MyMedListViewModel


class MyMedicationsAdapter(
    private val viewModel: MyMedListViewModel,
    var payload: MutableList<Payload> = ArrayList()
) :
    RecyclerView.Adapter<MyMedicationsAdapter.MyMedicationsViewHolder>() {
    lateinit var binding: AdapterMyMedicationsListBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyMedicationsViewHolder {
        context = parent.context
        binding =
            AdapterMyMedicationsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyMedicationsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return payload.size
    }

    override fun onBindViewHolder(holder: MyMedicationsViewHolder, position: Int) {
        holder.bind(payload[position].medlist, onItemClickListener)
    }


    inner class MyMedicationsViewHolder(private val itemBinding: AdapterMyMedicationsListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(medList: Medlist?, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = medList
            itemBinding.root.setOnClickListener {
                medList?.let { it1 ->
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

            val popup = android.widget.PopupMenu(context, optionsImg)
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

    fun addData(payload: ArrayList<Payload>) {
//        this.requestList.clear()
//        this.payload.addAll(medLists)
        this.payload = payload
        notifyDataSetChanged()
    }

}