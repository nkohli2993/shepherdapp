package com.app.shepherd.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.app.shepherd.databinding.AdapterRecommendedDocumentsBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.LockBoxViewModel


class RecommendedDocumentsAdapter(
    private val viewModel: LockBoxViewModel,
    var lockBoxTypes: MutableList<LockBoxTypes> = ArrayList()
) :
    RecyclerView.Adapter<RecommendedDocumentsAdapter.RecommendedDocumentsViewHolder>() {
    lateinit var binding: AdapterRecommendedDocumentsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.createRecommendedLockBoxDoc(itemData[0] as LockBoxTypes)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendedDocumentsViewHolder {
        context = parent.context
        binding =
            AdapterRecommendedDocumentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return RecommendedDocumentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return lockBoxTypes.size
    }

    override fun onBindViewHolder(holder: RecommendedDocumentsViewHolder, position: Int) {
        holder.bind(lockBoxTypes[position], onItemClickListener)
    }


    class RecommendedDocumentsViewHolder(private val itemBinding: AdapterRecommendedDocumentsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(lockBoxTypes: LockBoxTypes, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = lockBoxTypes
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    lockBoxTypes
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

    fun addData(lockBoxTypes: ArrayList<LockBoxTypes>) {
        this.lockBoxTypes.clear()
        this.lockBoxTypes.addAll(lockBoxTypes)
        notifyDataSetChanged()
    }

}