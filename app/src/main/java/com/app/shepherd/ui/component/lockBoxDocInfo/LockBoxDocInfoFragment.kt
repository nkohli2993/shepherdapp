package com.app.shepherd.ui.component.lockBoxDocInfo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentLockboxBinding
import com.app.shepherd.databinding.FragmentLockboxDocInfoBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.ui.component.lockBox.adapter.OtherDocumentsAdapter
import com.app.shepherd.ui.component.lockBox.adapter.RecommendedDocumentsAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class LockBoxDocInfoFragment : BaseFragment<FragmentLockboxBinding>(),
    View.OnClickListener {

    private val lockBoxDocInfoViewModel: LockBoxDocInfoViewModel by viewModels()

    private lateinit var fragmentLockboxDocInfoBinding: FragmentLockboxDocInfoBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLockboxDocInfoBinding =
            FragmentLockboxDocInfoBinding.inflate(inflater, container, false)

        return fragmentLockboxDocInfoBinding.root
    }

    override fun initViewBinding() {
        fragmentLockboxDocInfoBinding.listener = this
    }

    override fun observeViewModel() {
        observe(lockBoxDocInfoViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(lockBoxDocInfoViewModel.showSnackBar)
        observeToast(lockBoxDocInfoViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { lockBoxDocInfoViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentLockboxDocInfoBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentLockboxDocInfoBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonRename,R.id.buttonCancel -> {
                startActivity(Intent(requireContext(), HomeActivity::class.java))
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_lockbox_doc_info
    }


}

