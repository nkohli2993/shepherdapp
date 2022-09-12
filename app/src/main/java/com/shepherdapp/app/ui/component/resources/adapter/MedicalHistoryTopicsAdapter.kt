package com.shepherdapp.app.ui.component.resources.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.resource.AllResourceData
import com.shepherdapp.app.databinding.AdapterMedicalHistoryTopicsBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.ResourceViewModel
import com.squareup.picasso.Picasso


class MedicalHistoryTopicsAdapter(
    private val viewModel: ResourceViewModel,
    var resourceList: MutableList<AllResourceData> = ArrayList()
) :
    RecyclerView.Adapter<MedicalHistoryTopicsAdapter.MedicalHistoryTopicsViewHolder>() {
    lateinit var binding: AdapterMedicalHistoryTopicsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openSelectedResource(itemData[0] as AllResourceData)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicalHistoryTopicsViewHolder {
        context = parent.context
        binding =
            AdapterMedicalHistoryTopicsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MedicalHistoryTopicsViewHolder(binding)
    }

    override fun getItemCount(): Int {
          return resourceList.size
    }

    override fun onBindViewHolder(holder: MedicalHistoryTopicsViewHolder, position: Int) {
        holder.bind(resourceList[position], onItemClickListener)
    }


    class MedicalHistoryTopicsViewHolder(private val itemBinding: AdapterMedicalHistoryTopicsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(resourceData: AllResourceData, recyclerItemListener: RecyclerItemListener) {
            itemBinding.textViewTitle.text = resourceData.title
            if(resourceData.thumbnailUrl!=null && resourceData.thumbnailUrl!=""){
                Picasso.get().load(resourceData.thumbnailUrl)
                    .placeholder(R.drawable.image)
                    .into(itemBinding.imageViewTopic)
            }
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    resourceData
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

    fun addData(resourceList: MutableList<AllResourceData>, isSearch: Boolean) {
        if(isSearch){
            this.resourceList.clear()
            this.resourceList.addAll(resourceList)
        }
        else{
            this.resourceList.addAll(resourceList)
        }

        notifyDataSetChanged()
    }

}