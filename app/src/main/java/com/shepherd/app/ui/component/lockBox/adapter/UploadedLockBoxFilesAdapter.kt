package com.shepherd.app.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.databinding.AdapterUploadedFilesBinding
import com.shepherd.app.view_model.AddNewLockBoxViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class UploadedLockBoxFilesAdapter(
    private val viewModel: AddNewLockBoxViewModel,
//    var lockBoxList: MutableList<LockBox> = ArrayList()
    var selectedFiles: MutableList<File> = ArrayList()
) :
    RecyclerView.Adapter<UploadedLockBoxFilesAdapter.UploadedDocumentsViewHolder>() {
    lateinit var binding: AdapterUploadedFilesBinding
    lateinit var context: Context

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(file: File)
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
        return selectedFiles.size
    }

    override fun onBindViewHolder(holder: UploadedDocumentsViewHolder, position: Int) {
        holder.bind(selectedFiles[position])
    }


    inner class UploadedDocumentsViewHolder(private val itemBinding: AdapterUploadedFilesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(file: File) {
//            itemBinding.data = lockBox

//            val createdAt = lockBox.createdAt

            val sdf = SimpleDateFormat("MMM dd, yyyy")
            val currentDate = sdf.format(Date())
            /*val formattedString = date.toTextFormat(
                date,
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "MMM dd, yyyy"
            )*/
            itemBinding.let {
                it.txtFolderName.text = file.name
                it.txtUploadDate.text = "Uploaded $currentDate"
            }

            itemBinding.imgDelete.setOnClickListener {
//                selectedFiles.remove(file)
//                notifyDataSetChanged()

                onItemClickListener?.onItemClick(file)
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(selectedFiles: ArrayList<File>) {
        this.selectedFiles.clear()
        this.selectedFiles.addAll(selectedFiles)
        notifyDataSetChanged()
    }

    fun removeData(file: File) {
        selectedFiles.remove(file)
        notifyDataSetChanged()
    }

}