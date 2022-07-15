package com.app.shepherd.ui.component.loved_one

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentInvitationBinding
import com.app.shepherd.databinding.FragmentLovedOnesBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.joinCareTeam.adapter.JoinCareTeamAdapter
import com.app.shepherd.utils.Const
import com.app.shepherd.view_model.CareTeamsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_join_care_team.*

@AndroidEntryPoint
class LovedOnesFragment : BaseFragment<FragmentLovedOnesBinding>(), View.OnClickListener {

    private lateinit var fragmentLovedOnesBinding: FragmentLovedOnesBinding
    private var lovedOneAdapter: LovedOneAdapter? = null
    private val lovedOneViewModel: LovedOneViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLovedOnesBinding =
            FragmentLovedOnesBinding.inflate(inflater, container, false)

        return fragmentLovedOnesBinding.root
    }

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        fragmentLovedOnesBinding.listener = this
        setLoveOneAdapter()
    }

    private fun setLoveOneAdapter() {
        lovedOneAdapter = LovedOneAdapter(lovedOneViewModel)
        recyclerViewMembers.adapter = lovedOneAdapter
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_loved_ones
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvNew -> {
                findNavController().navigate(
                    LovedOnesFragmentDirections.actionNavLovedOneToNavAddLovedOne(
                        source = Const.ADD_LOVE_ONE
                    )
                )
            }
        }
    }
}