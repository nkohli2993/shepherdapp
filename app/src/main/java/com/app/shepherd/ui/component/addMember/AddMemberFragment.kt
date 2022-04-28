package com.app.shepherd.ui.component.addMember

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.careTeamMembers.adapter.CareTeamMembersAdapter
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.observe
import com.app.shepherd.utils.setupSnackbar
import com.app.shepherd.utils.showToast
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.viewModels
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.ui.component.addMember.adapter.AddMemberRoleAdapter
import com.app.shepherd.ui.component.addMember.adapter.RestrictionsModuleAdapter
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddMemberFragment : BaseFragment<FragmentAddMemberBinding>(),
    View.OnClickListener {

    private val addMemberViewModel: AddMemberViewModel by viewModels()

    private lateinit var fragmentAddMemberBinding: FragmentAddMemberBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddMemberBinding =
            FragmentAddMemberBinding.inflate(inflater, container, false)

        return fragmentAddMemberBinding.root
    }

    override fun initViewBinding() {
        fragmentAddMemberBinding.listener = this

        setRoleAdapter()
        setRestrictionModuleAdapter()

    }

    override fun observeViewModel() {
        observe(addMemberViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(addMemberViewModel.showSnackBar)
        observeToast(addMemberViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { addMemberViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentAddMemberBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentAddMemberBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setRoleAdapter() {
        val addMemberRoleAdapter = AddMemberRoleAdapter(addMemberViewModel)
        fragmentAddMemberBinding.recyclerViewMemberRole.adapter = addMemberRoleAdapter

    }

    private fun setRestrictionModuleAdapter() {
        val restrictionsModuleAdapter = RestrictionsModuleAdapter(addMemberViewModel)
        fragmentAddMemberBinding.recyclerViewModules.adapter = restrictionsModuleAdapter

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {

            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_member
    }




}

