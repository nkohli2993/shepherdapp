package com.app.shepherd.ui.component.lockBox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentAddNewLockBoxBinding
import com.app.shepherd.databinding.FragmentLockboxBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.lockBox.adapter.UploadedFilesAdapter
import com.app.shepherd.utils.*
import com.app.shepherd.view_model.LockBoxViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Deepak Rattan on 22-07-22
 */
@AndroidEntryPoint
class AddNewLockBoxFragment : BaseFragment<FragmentLockboxBinding>(),
    View.OnClickListener {

//    private val lockBoxViewModel: LockBoxViewModel by viewModels()

    private lateinit var fragmentAddNewLockBoxBinding: FragmentAddNewLockBoxBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddNewLockBoxBinding =
            FragmentAddNewLockBoxBinding.inflate(inflater, container, false)

        return fragmentAddNewLockBoxBinding.root
    }

    override fun initViewBinding() {
        fragmentAddNewLockBoxBinding.listener = this

        setUploadedFilesAdapter()


    }

    override fun observeViewModel() {
    }





    private fun setUploadedFilesAdapter() {
//        val uploadedFilesAdapter = UploadedFilesAdapter(lockBoxViewModel)
//        fragmentAddNewLockBoxBinding.rvUploadedFiles.adapter = uploadedFilesAdapter

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            /* R.id.buttonNewDocument -> {
                 p0.findNavController().navigate(R.id.action_lock_box_to_lock_box_doc_info)
             }*/
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_lockbox
    }


}

