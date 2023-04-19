package com.shepherdapp.app.ui.component.lockBox.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.shepherdapp.app.databinding.AdapterRecommendedDocumentsBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.CareRole
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Modules
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.view_model.LockBoxViewModel


class RecommendedDocumentsAdapter(
    private val viewModel: LockBoxViewModel,
    var lockBoxTypes: MutableList<LockBoxTypes> = ArrayList()
) :
    RecyclerView.Adapter<RecommendedDocumentsAdapter.RecommendedDocumentsViewHolder>() {
    lateinit var binding: AdapterRecommendedDocumentsBinding
    lateinit var context: Context

    private val TAG = "RecommendedDocument"


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


    inner class RecommendedDocumentsViewHolder(private val itemBinding: AdapterRecommendedDocumentsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(lockBoxTypes: LockBoxTypes, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = lockBoxTypes
            // Check if loggedIn User is CareTeam Leader for the selected lovedOne
            val lovedOneDetail = viewModel.getLovedOneDetail()
            val isNewVisible = when (lovedOneDetail?.careRoles?.slug) {
                CareRole.CareTeamLead.slug -> {
                    itemBinding.root.isClickable = true
                    true
                }
                else -> {
                    itemBinding.root.isClickable = false
                    false
                }
            }

            // Check permissions
            val permissions = Prefs.with(ShepherdApp.appContext)?.getString(Const.PERMISSIONS, "")
            Log.d(TAG, "bind: Permissions ->$permissions")
            val lockBoxPermission = permissions?.split(",")?.filter {
                it == Modules.LockBox.value
            }
            Log.d(TAG, "bind: permissions -> $lockBoxPermission")

            // "lockBox" key represents array. If it is not empty, means document has been uploaded
            // So select the checkbox accordingly
            if (lockBoxTypes.lockbox.isNotEmpty()) {
                // Check if lockBox permission is available or not
                itemBinding.checkbox.isChecked = !lockBoxPermission.isNullOrEmpty()
            }


            itemBinding.root.setOnClickListener {
                if (isNewVisible) {
                    recyclerItemListener.onItemSelected(
                        lockBoxTypes
                    )
                }

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

    fun clearData(){
        this.lockBoxTypes.clear()
        notifyDataSetChanged()

    }

}