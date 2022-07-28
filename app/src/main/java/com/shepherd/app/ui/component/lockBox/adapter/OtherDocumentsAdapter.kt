package com.shepherd.app.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherd.app.databinding.AdapterOtherDocumentsBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.extensions.toTextFormat
import com.shepherd.app.view_model.LockBoxViewModel


class OtherDocumentsAdapter(
    private val viewModel: LockBoxViewModel,
    var lockBoxList: MutableList<LockBox> = ArrayList()
) :
    RecyclerView.Adapter<OtherDocumentsAdapter.OtherDocumentsViewHolder>() {
    lateinit var binding: AdapterOtherDocumentsBinding
    lateinit var context: Context

    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openLockBoxDocDetail(itemData[0] as LockBox)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OtherDocumentsViewHolder {
        context = parent.context
        binding =
            AdapterOtherDocumentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return OtherDocumentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return lockBoxList.size
    }

    override fun onBindViewHolder(holder: OtherDocumentsViewHolder, position: Int) {
        holder.bind(lockBoxList[position], onItemClickListener)
    }


    class OtherDocumentsViewHolder(private val itemBinding: AdapterOtherDocumentsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(lockBox: LockBox, recyclerItemListener: RecyclerItemListener) {
            val createdAt = lockBox.createdAt
            val formattedString = createdAt?.toTextFormat(
                createdAt,
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "MMM dd, yyyy"
            )

            itemBinding.let {
                it.txtTitle.text = lockBox.name
                it.txtUploadedDate.text = formattedString

            }

            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    lockBox
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

    fun addData(lockBoxList: ArrayList<LockBox>) {
        this.lockBoxList.clear()
        this.lockBoxList.addAll(lockBoxList)
        notifyDataSetChanged()
    }

}