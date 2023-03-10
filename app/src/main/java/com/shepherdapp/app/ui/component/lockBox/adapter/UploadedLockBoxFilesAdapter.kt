package com.shepherdapp.app.ui.component.lockBox.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.databinding.AdapterUploadedFilesBinding
import com.shepherdapp.app.view_model.AddNewLockBoxViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class UploadedLockBoxFilesAdapter(
    private val viewModel: AddNewLockBoxViewModel,
    var selectedFiles: MutableList<File> = ArrayList()
) :
    RecyclerView.Adapter<UploadedLockBoxFilesAdapter.UploadedDocumentsViewHolder>() {
    lateinit var binding: AdapterUploadedFilesBinding
    lateinit var context: Context

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(file: File,position: Int)
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
            val sdf = SimpleDateFormat("MMM dd, yyyy")
            val currentDate = sdf.format(Calendar.getInstance().time)
            itemBinding.let {
                it.txtFolderName.text = file.name
                it.txtUploadDate.text = "Uploaded $currentDate"
            }

            itemBinding.imgDelete.setOnClickListener {
                onItemClickListener?.onItemClick(file,bindingAdapterPosition)
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
//        this.selectedFiles.clear()
        this.selectedFiles.addAll(selectedFiles)
        notifyDataSetChanged()
    }

    fun removeData(file: File) {
        selectedFiles.remove(file)
        notifyDataSetChanged()
    }
    fun removeData(file: Int) {
        selectedFiles.removeAt(file)
        notifyDataSetChanged()
    }

}