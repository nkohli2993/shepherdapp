package com.shepherdapp.app.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.lock_box.edit_lock_box.DocumentData
import com.shepherdapp.app.databinding.AdapterUploadedFileLockboxBinding
import com.shepherdapp.app.utils.extensions.convertISOTimeToDate
import com.shepherdapp.app.utils.extensions.toTextFormat
import java.util.*

class UploadedDocumentAdapter(
    var selectedFiles: MutableList<DocumentData> = ArrayList()
) :
    RecyclerView.Adapter<UploadedDocumentAdapter.UploadedDocumentsViewHolder>() {
    lateinit var binding: AdapterUploadedFileLockboxBinding
    lateinit var context: Context

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
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
            AdapterUploadedFileLockboxBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UploadedDocumentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return selectedFiles.size
    }

    override fun onBindViewHolder(holder: UploadedDocumentsViewHolder, position: Int) {
        holder.bind(selectedFiles[position])
    }


    inner class UploadedDocumentsViewHolder(private val itemBinding: AdapterUploadedFileLockboxBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(file: DocumentData) {
            itemBinding.let {

                it.txtFolderName.text = file.filePath.substring(file.filePath.lastIndexOf("/") + 1)
                it.txtUploadDate.text = "Uploaded ${file.uploadDate.toTextFormat(file.uploadDate,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    "MMMM dd, yyyy")}"
                if (file.filePath.lowercase()
                        .endsWith(".png") || file.filePath.lowercase()
                        .endsWith(".jpg") || file.filePath.lowercase()
                        .endsWith("jpeg") || file.filePath.lowercase()
                        .endsWith("gif") || file.filePath.lowercase()
                        .endsWith("heic") || file.filePath.lowercase()
                        .endsWith("heif")
                ) {
                    it.imgFolder.setImageResource(R.drawable.ic_image)
                } else if (file.filePath.lowercase().endsWith(".pdf") || file.filePath.lowercase()
                        .endsWith(".pdf/x") || file.filePath.lowercase()
                        .endsWith(".pdf/a") || file.filePath.lowercase().endsWith(".pdf/e")
                ) {
                    it.imgFolder.setImageResource(R.drawable.ic_pdf)
                } else if (file.filePath.lowercase().endsWith(".doc") || file.filePath.lowercase()
                        .endsWith(".docm") || file.filePath.lowercase()
                        .endsWith(".docx") || file.filePath.lowercase().endsWith(".txt")
                ) {
                    it.imgFolder.setImageResource(R.drawable.ic_doc)
                } else {
                    it.imgFolder.setImageResource(R.drawable.ic_image)
                }
            }

            itemBinding.imgDelete.setOnClickListener {
                onItemClickListener?.onItemClick(absoluteAdapterPosition)
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(selectedFiles: ArrayList<DocumentData>) {
        this.selectedFiles.addAll(selectedFiles)
        notifyDataSetChanged()
    }

    fun removeData(position: Int) {
        selectedFiles.removeAt(position)
        notifyDataSetChanged()
    }

}