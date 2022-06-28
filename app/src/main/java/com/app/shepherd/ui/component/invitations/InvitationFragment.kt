package com.app.shepherd.ui.component.invitations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.databinding.FragmentInvitationBinding
import com.app.shepherd.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvitationFragment : BaseFragment<FragmentInvitationBinding>(), View.OnClickListener,
    InvitationAdapter.OnItemClickListener {
    private lateinit var fragmentInvitationBinding: FragmentInvitationBinding
    private val invitationViewModel: InvitationViewModel by viewModels()
    private var invitationAdapter: InvitationAdapter? = null

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
        setInvitationAdapter()
    }

    override fun initViewBinding() {
        fragmentInvitationBinding.listener = this
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

    override fun onItemClick(careTeam: CareTeam) {

    }

}