package com.app.shepherd.ui.component.lockBox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentLockboxBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.lockBox.adapter.OtherDocumentsAdapter
import com.app.shepherd.ui.component.lockBox.adapter.RecommendedDocumentsAdapter
import com.app.shepherd.ui.component.myMedList.adapter.MyMedicationsAdapter
import com.app.shepherd.ui.component.myMedList.adapter.MyRemindersAdapter
import com.app.shepherd.ui.component.myMedList.adapter.SelectedDayMedicineAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class LockBoxFragment : BaseFragment<FragmentLockboxBinding>(),
    View.OnClickListener {

    private val lockBoxViewModel: LockBoxViewModel by viewModels()

    private lateinit var fragmentLockboxBinding: FragmentLockboxBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLockboxBinding =
            FragmentLockboxBinding.inflate(inflater, container, false)

        return fragmentLockboxBinding.root
    }

    override fun initViewBinding() {
        fragmentLockboxBinding.listener = this

        setRecommendedDocumentsAdapter()
        setOtherDocumentsAdapter()


    }

    override fun observeViewModel() {
        observe(lockBoxViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(lockBoxViewModel.showSnackBar)
        observeToast(lockBoxViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { lockBoxViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentLockboxBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentLockboxBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setRecommendedDocumentsAdapter() {
        val myRemindersAdapter = RecommendedDocumentsAdapter(lockBoxViewModel)
        fragmentLockboxBinding.recyclerViewDocuments.adapter = myRemindersAdapter

    }

    private fun setOtherDocumentsAdapter() {
        val myMedicationsAdapter = OtherDocumentsAdapter(lockBoxViewModel)
        fragmentLockboxBinding.recyclerViewOtherDocuments.adapter = myMedicationsAdapter

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonNewDocument -> {
                p0.findNavController().navigate(R.id.action_lock_box_to_lock_box_doc_info)
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_lockbox
    }


}

