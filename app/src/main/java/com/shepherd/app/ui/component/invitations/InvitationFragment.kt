package com.shepherd.app.ui.component.invitations

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.dto.invitation.Results
import com.shepherd.app.databinding.FragmentInvitationBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.Invitations
import com.shepherd.app.utils.Status
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.view_model.InvitationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvitationFragment : BaseFragment<FragmentInvitationBinding>(), View.OnClickListener,
    InvitationAdapter.OnItemClickListener {
    private var results: ArrayList<Results>? = ArrayList()
    private lateinit var fragmentInvitationBinding: FragmentInvitationBinding
    private val invitationViewModel: InvitationViewModel by viewModels()
    private var invitationAdapter: InvitationAdapter? = null
    private var sendType: String = Invitations.Receiver.sendType
    private var status = Status.Zero.status

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentInvitationBinding =
            FragmentInvitationBinding.inflate(inflater, container, false)

        return fragmentInvitationBinding.root
    }

    override fun observeViewModel() {
        invitationViewModel.invitationsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    //showError(requireContext(), it.message.toString())

                    results?.clear()
                    results?.let { it1 -> invitationAdapter?.updateInvitations(it1) }

                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Join CareTeam Invitations")
                        setMessage("No Invitations Found")
                        setPositiveButton("OK") { _, _ ->
                            // navigateToDashboardScreen()
                            findNavController().popBackStack()
                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    results = it.data.payload?.results
                    if (results.isNullOrEmpty()) return@observeEvent
                    invitationAdapter?.updateInvitations(results!!)
                }
            }
        }

        invitationViewModel.acceptInvitationsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(requireContext(), "Invitation Accepted Successfully...")
                    invitationViewModel.getJoinCareTeamInvitations(sendType, status)
                }
            }
        }


    }

    override fun initViewBinding() {
        fragmentInvitationBinding.listener = this

        setInvitationAdapter()

        //Get Invitations for Joining Care Team
        invitationViewModel.getJoinCareTeamInvitations(sendType, status)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_invitation
    }

    private fun setInvitationAdapter() {
        invitationAdapter = InvitationAdapter(invitationViewModel)
        invitationAdapter?.setClickListener(this)
        fragmentInvitationBinding.rvInvitation.adapter = invitationAdapter
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }

    override fun onItemClick(id: Int?) {
        if (id != null) {
            // Accept the invitation request
            invitationViewModel.acceptCareTeamInvitations(id)
        }
    }


}