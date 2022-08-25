package com.shepherd.app.ui.component.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeam
import com.shepherd.app.data.dto.dashboard.DashboardModel
import com.shepherd.app.data.dto.dashboard.Payload
import com.shepherd.app.databinding.FragmentDashboardBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherd.app.ui.component.dashboard.adapter.CareTeamMembersDashBoardAdapter
import com.shepherd.app.ui.component.dashboard.adapter.DashboardAdapter
import com.shepherd.app.ui.component.home.HomeActivity
import com.shepherd.app.utils.Modules
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.observeEvent
import com.shepherd.app.view_model.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(),
    View.OnClickListener {

    private lateinit var fragmentDashboardBinding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1
    private var careTeams: ArrayList<CareTeam>? = ArrayList()
    private var dashBoardAdapter: DashboardAdapter? = null
    private var careTeamMembersDashBoardAdapter: CareTeamMembersDashBoardAdapter? = null
    private var parentActivityListener: ChildFragmentToActivityListener? = null

    private lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
        if (context is ChildFragmentToActivityListener) parentActivityListener = context
        else throw RuntimeException("$context must implement ChildFragmentToActivityListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.inflateDashboardList(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentDashboardBinding = FragmentDashboardBinding.inflate(inflater, container, false)
        //Get Home Data
        viewModel.getHomeData()
        return fragmentDashboardBinding.root
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_dashboard
    }

    override fun observeViewModel() {
        observeEvent(viewModel.openDashboardItems, ::navigateToDashboardItems)
        // Observe Get Home Data Api Response
        viewModel.homeResponseLiveData.observeEvent(this) {
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
                    initHomeViews(it.data.payload)
                }
            }
        }
    }

    private fun initHomeViews(payload: Payload?) {
        fragmentDashboardBinding.let {
            // Care Points
            it.tvTaskCount.text = payload?.carePoints.toString()

            // Discussions

            // MedList
            it.tvMedListMessageCount.text = payload?.medLists.toString()

            // Resources
            // LockBox
            it.tvLockBoxCount.text = payload?.lockBoxs.toString()

            // Vital Stats

            // Care Team
            val careTeamMembers = payload?.careTeams


            val careTeamMembersProfileList = payload?.careTeamProfiles?.map { careTeamProfiles ->
                careTeamProfiles.user?.profilePhoto
            } as ArrayList<String>

            val careTeamMembersCount = careTeamMembersProfileList.size

            /* if (careTeamMembersCount == 0 || careTeamMembersCount == 1) {
                 fragmentDashboardBinding.tvMember.text = "$careTeamMembersCount Member"
             } else {
                 fragmentDashboardBinding.tvMember.text = "$careTeamMembersCount Members"
             }*/

            if (!careTeamMembersProfileList.isNullOrEmpty()) {
                careTeamMembersDashBoardAdapter?.addData(careTeamMembersProfileList)
            }

            permissionCards(View.VISIBLE)
            // show accessed cards only to users
/*
            if (!viewModel.getUUID().isNullOrEmpty() && viewModel.getLovedUserDetail() != null) {
                if (viewModel.getUUID() == viewModel.getLovedUserDetail()?.userId)
                    if (viewModel.getLovedUserDetail() != null) {
                        val perList = viewModel.getLovedUserDetail()?.permission?.split(',')
                            ?.map { it.trim() }
                        permissionCards(View.GONE)
                        for (i in perList?.indices!!) {
                            checkPermission(perList[i].toInt())
                        }
                    } else {
                        permissionCards(View.VISIBLE)
                    }
            } else {
                permissionCards(View.VISIBLE)
            }
*/
        }
    }

    private fun permissionCards(value: Int) {
        fragmentDashboardBinding.cvCarePoints.visibility = value
        fragmentDashboardBinding.cvLockBox.visibility = value
        fragmentDashboardBinding.cvMedList.visibility = value
        fragmentDashboardBinding.cvResources.visibility = value
        fragmentDashboardBinding.cvCareTeam.visibility = View.VISIBLE
        fragmentDashboardBinding.cvVitalStats.visibility = View.VISIBLE
        fragmentDashboardBinding.cvDiscussion.visibility = View.VISIBLE
    }

    private fun checkPermission(permission: Int?) {
        when {
            Modules.CareTeam.value == permission -> {
                fragmentDashboardBinding.cvCarePoints.visibility = View.VISIBLE
            }
            Modules.LockBox.value == permission -> {
                fragmentDashboardBinding.cvLockBox.visibility = View.VISIBLE
            }
            Modules.MedList.value == permission -> {
                fragmentDashboardBinding.cvMedList.visibility = View.VISIBLE
            }
            Modules.Resources.value == permission -> {
                fragmentDashboardBinding.cvResources.visibility = View.VISIBLE
            }
        }

    }

    override fun initViewBinding() {
        fragmentDashboardBinding.listener = this
        setCareTeamAdapters()
    }

    private fun setCareTeamAdapters() {
        careTeamMembersDashBoardAdapter = CareTeamMembersDashBoardAdapter(viewModel)
        fragmentDashboardBinding.rvCareTeam.adapter = careTeamMembersDashBoardAdapter
    }

    private fun navigateToDashboardItems(navigateEvent: SingleEvent<DashboardModel>) {
        navigateEvent.getContentIfNotHandled()?.let {

        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvCarePoints -> {
                findNavController().navigate(R.id.action_dashboard_to_care_points)
            }
            R.id.cvDiscussion -> {
                findNavController().navigate(R.id.action_dashboard_to_messages)
            }
            R.id.cvMedList -> {
                findNavController().navigate(R.id.action_dashboard_to_medication_list)
            }
            R.id.cvResources -> {
                findNavController().navigate(R.id.action_dashboard_to_resources)
            }
            R.id.cvLockBox -> {
                findNavController().navigate(R.id.action_dashboard_to_lock_box)
            }
            R.id.cvVitalStats -> {
                findNavController().navigate(R.id.action_dashboard_to_vital_stats)
            }
            R.id.cvCareTeam -> {
                findNavController().navigate(R.id.action_dashboard_to_care_team_members)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        parentActivityListener?.msgFromChildFragmentToActivity()
    }

}