package com.shepherdapp.app.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherdapp.app.databinding.AdapterOtherDocumentsBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.ClickType
import com.shepherdapp.app.utils.extensions.toTextFormat
import com.shepherdapp.app.view_model.LockBoxViewModel


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
        return OtherDocumentsViewHolder(binding, context)
    }

    override fun getItemCount(): Int {
        return lockBoxList.size
    }

    override fun onBindViewHolder(holder: OtherDocumentsViewHolder, position: Int) {
        holder.bind(lockBoxList[position], onItemClickListener)
    }


    class OtherDocumentsViewHolder(
        private val itemBinding: AdapterOtherDocumentsBinding,
        val context: Context
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(lockBox: LockBox, recyclerItemListener: RecyclerItemListener) {
            val createdAt = lockBox.createdAt
            val formattedString = createdAt?.toTextFormat(
                createdAt,
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "MMMM dd, yyyy"
            )

            itemBinding.let {
                it.txtTitle.text = lockBox.name
                it.txtUploadedDate.text = formattedString
            }

            itemBinding.imgMore.setOnClickListener {
                openDocumentOptions(
                    absoluteAdapterPosition,
                    itemBinding.imgMore,
                    context,
                    recyclerItemListener,
                    lockBox
                )
            }
            /*itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    lockBox
                )
            }*/
        }

        private fun openDocumentOptions(
            position: Int,
            optionsImg: AppCompatImageView,
            context: Context,
            recyclerItemListener: RecyclerItemListener,
            lockBox: LockBox
        ) {

            val popup = PopupMenu(context, optionsImg)
            popup.inflate(R.menu.options_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.view_document -> {
                        lockBox.clickType = ClickType.View.value
                        recyclerItemListener.onItemSelected(
                            lockBox
                        )
                        true
                    }
                    R.id.delete_document -> {
                        lockBox.clickType = ClickType.Delete.value
                        lockBox.deletePosition = position
                        recyclerItemListener.onItemSelected(
                            lockBox
                        )
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(lockBoxList: ArrayList<LockBox>, isSearchData: Boolean) {
        if (isSearchData) {
            this.lockBoxList = lockBoxList
        } else {
            this.lockBoxList.addAll(lockBoxList)
        }
        notifyDataSetChanged()
    }

    fun setList(lockBoxList: ArrayList<LockBox>) {
        this.lockBoxList = lockBoxList
        notifyDataSetChanged()
    }

}