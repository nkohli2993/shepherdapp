package com.shepherd.app.ui.component.lockBox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentEditLockBoxBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.lockBox.adapter.UploadedLockBoxFilesAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


/**
 * Created by Nikita Kohli on 07-09-22
 */
@AndroidEntryPoint
class EditLockBoxFragment : BaseFragment<FragmentEditLockBoxBinding>(),
    View.OnClickListener, UploadedLockBoxFilesAdapter.OnItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_lock_box, container, false)
    }

    override fun observeViewModel() {

    }

    override fun initViewBinding() {

    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_edit_lock_box
    }

    override fun onClick(p0: View?) {

    }

    override fun onItemClick(file: File) {

    }

}