package com.shepherd.app.ui.component.resources.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.resource.AllResourceData
import com.shepherd.app.databinding.AdapterTopicsBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.ResourceViewModel
import com.squareup.picasso.Picasso


class TopicsAdapter(
    private val viewModel: ResourceViewModel,
    var trendingResourceList: MutableList<AllResourceData> = ArrayList()
) :
    RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {
    lateinit var binding: AdapterTopicsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openSelectedResource(itemData[0] as Int)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopicsViewHolder {
        context = parent.context
        binding =
            AdapterTopicsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TopicsViewHolder(binding)
    }

    override fun getItemCount(): Int {
          return trendingResourceList.size
    }

    override fun onBindViewHolder(holder: TopicsViewHolder, position: Int) {
        holder.bind(trendingResourceList[position], onItemClickListener)
    }


    inner class TopicsViewHolder(private val itemBinding: AdapterTopicsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(resourceData: AllResourceData, recyclerItemListener: RecyclerItemListener) {
            itemBinding.textViewTitle.text = resourceData.title
            if(resourceData.thumbnailUrl!=null && resourceData.thumbnailUrl!=""){
                Picasso.get().load(resourceData.thumbnailUrl)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(itemBinding.imageViewTopic)
            }
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

    fun addData(dashboard: MutableList<AllResourceData>) {
        this.trendingResourceList.addAll(dashboard)
        notifyDataSetChanged()
    }

}