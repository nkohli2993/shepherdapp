package com.app.shepherd.ui.component.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentProfileBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.profile.adapter.LovedOnesAdapter
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
class ProfileFragment : BaseFragment<FragmentProfileBinding>(), View.OnClickListener {

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var fragmentProfileBinding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfileBinding =
            FragmentProfileBinding.inflate(inflater, container, false)

        return fragmentProfileBinding.root
    }

    override fun initViewBinding() {
        fragmentProfileBinding.listener = this
        setLovedOnesAdapter()
        setPendingInvitationsAdapter()
    }

    override fun observeViewModel() {
        observe(profileViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(profileViewModel.showSnackBar)
        observeToast(profileViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { profileViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentProfileBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentProfileBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setLovedOnesAdapter() {
        val lovedOnesAdapter = LovedOnesAdapter(profileViewModel)
        fragmentProfileBinding.recyclerLovedOnes.adapter = lovedOnesAdapter


    }

    private fun setPendingInvitationsAdapter() {
//        val pendingInvitationsAdapter = PendingInvitationsAdapter(profileViewModel)
//        fragmentProfileBinding.recyclerPendingInvitations.adapter = pendingInvitationsAdapter

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvChange -> {
                p0.findNavController().navigate(R.id.action_nav_profile_to_changePassword)
            }
            R.id.tvReset -> {
                p0.findNavController().navigate(R.id.action_nav_profile_to_secureCode)
            }
            R.id.clProfileWrapper -> {
                p0.findNavController().navigate(R.id.action_nav_profile_to_editProfile)
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_profile
    }


}

