package com.shepherd.app.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherd.app.databinding.AdapterUploadedFilesBinding
import com.shepherd.app.utils.extensions.toTextFormat
import com.shepherd.app.view_model.AddNewLockBoxViewModel


class UploadedLockBoxFilesAdapter(
    private val viewModel: AddNewLockBoxViewModel,
    var lockBoxList: MutableList<LockBox> = ArrayList()
) :
    RecyclerView.Adapter<UploadedLockBoxFilesAdapter.UploadedDocumentsViewHolder>() {
    lateinit var binding: AdapterUploadedFilesBinding
    lateinit var context: Context

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(lockBox: LockBox)
    }

    fun setClickListener(clickListener: OnItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UploadedDocumentsViewHolder {
        context = parent.context
        binding =
            AdapterUploadedFilesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UploadedDocumentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return lockBoxList.size
    }

    override fun onBindViewHolder(holder: UploadedDocumentsViewHolder, position: Int) {
        holder.bind(lockBoxList[position])
    }


    inner class UploadedDocumentsViewHolder(private val itemBinding: AdapterUploadedFilesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(lockBox: LockBox) {
            itemBinding.data = lockBox

            val createdAt = lockBox.createdAt
            val formattedString = createdAt?.toTextFormat(
                createdAt,
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "MMM dd, yyyy"
            )
            itemBinding.let {
                it.txtFolderName.text = lockBox.name
                it.txtUploadDate.text = "Uploaded $formattedString"
            }

            itemBinding.imgDelete.setOnClickListener {
                onItemClickListener?.onItemClick(lockBox)
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