package com.shepherdapp.app.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.shepherdapp.app.databinding.AdapterRecommendedDocumentsBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.LockBoxViewModel


class RecommendedDocumentsAdapter(
    private val viewModel: LockBoxViewModel,
    var lockBoxTypes: MutableList<LockBoxTypes> = ArrayList()
) :
    RecyclerView.Adapter<RecommendedDocumentsAdapter.RecommendedDocumentsViewHolder>() {
    lateinit var binding: AdapterRecommendedDocumentsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            val lb = itemData[0] as LockBoxTypes
            if (lb.lockbox.isNotEmpty()) {
                // Recommended LockBox doc is already uploaded. So need to view the uploaded recommended doc
                viewModel.viewRecommendedLockBoxDOc(lb)
            } else {
                // Recommended LockBox doc is not uploaded and need to create it
                viewModel.createRecommendedLockBoxDoc(lb)
            }
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
//            itemBinding.ivStatus.setImageResource(R.drawable.ic_dot)
            /* if(lockBoxTypes.isAdded){
                 itemBinding.ivStatus.setImageResource(R.drawable.ic_check)
             }*/

            // "lockbox" key represents array. If it is not empty, means document has been uploaded
            // So select the checkbox accordingly
            if (lockBoxTypes.lockbox.isNotEmpty()) {
                itemBinding.checkbox.isChecked = true
            }

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