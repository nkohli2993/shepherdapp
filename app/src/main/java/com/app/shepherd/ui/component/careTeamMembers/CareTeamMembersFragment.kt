package com.app.shepherd.ui.component.careTeamMembers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentCareTeamMembersBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.careTeamMembers.adapter.CareTeamMembersAdapter
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.observe
import com.app.shepherd.utils.setupSnackbar
import com.app.shepherd.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class CareTeamMembersFragment : BaseFragment<FragmentCareTeamMembersBinding>(),
    View.OnClickListener {

    private val careTeamViewModel: CareTeamMembersViewModel by viewModels()

    private lateinit var fragmentCareTeamMembersBinding: FragmentCareTeamMembersBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCareTeamMembersBinding =
            FragmentCareTeamMembersBinding.inflate(inflater, container, false)

        return fragmentCareTeamMembersBinding.root
    }

    override fun initViewBinding() {
        fragmentCareTeamMembersBinding.listener = this
        setCareTeamAdapters()
    }

    override fun observeViewModel() {
        observe(careTeamViewModel.loginLiveData, ::handleLoginResult)
        observe(careTeamViewModel.openMemberDetails, ::openMemberDetails)
        observeSnackBarMessages(careTeamViewModel.showSnackBar)
        observeToast(careTeamViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { careTeamViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentCareTeamMembersBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentCareTeamMembersBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun setCareTeamAdapters() {
        val careTeamAdapter = CareTeamMembersAdapter(careTeamViewModel)
        fragmentCareTeamMembersBinding.recyclerViewCareTeam.adapter = careTeamAdapter

    }

    private fun openMemberDetails(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
            findNavController().navigate(R.id.action_care_team_members_to_member_details)
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonAddNewMember -> {
                findNavController().navigate(R.id.action_care_team_members_to_add_team_member)
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_team_members
    }


}

