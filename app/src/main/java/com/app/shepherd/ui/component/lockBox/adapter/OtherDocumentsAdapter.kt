package com.app.shepherd.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.app.shepherd.databinding.AdapterOtherDocumentsBinding
import com.app.shepherd.utils.extensions.toTextFormat
import com.app.shepherd.view_model.LockBoxViewModel


class OtherDocumentsAdapter(
    private val viewModel: LockBoxViewModel,
    var lockBoxList: MutableList<LockBox> = ArrayList()
) :
    RecyclerView.Adapter<OtherDocumentsAdapter.OtherDocumentsViewHolder>() {
    lateinit var binding: AdapterOtherDocumentsBinding
    lateinit var context: Context

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
        holder.bind(lockBoxList[position])
    }


    class OtherDocumentsViewHolder(private val itemBinding: AdapterOtherDocumentsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(lockBox: LockBox) {
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