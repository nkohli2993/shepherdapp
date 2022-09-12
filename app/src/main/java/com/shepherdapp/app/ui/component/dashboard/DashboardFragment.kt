package com.shepherdapp.app.ui.component.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.dashboard.DashboardModel
import com.shepherdapp.app.data.dto.dashboard.Payload
import com.shepherdapp.app.databinding.FragmentDashboardBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherdapp.app.ui.component.dashboard.adapter.CareTeamMembersDashBoardAdapter
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.utils.Modules
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.observeEvent
import com.shepherdapp.app.view_model.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(),
    View.OnClickListener {

    private lateinit var fragmentDashboardBinding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
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

            // Care Team
            val careTeamMembersProfileList = payload?.careTeamProfiles?.map { careTeamProfiles ->
                careTeamProfiles.user?.profilePhoto
            } as ArrayList<String>

            if (!careTeamMembersProfileList.isNullOrEmpty()) {
                careTeamMembersDashBoardAdapter?.addData(careTeamMembersProfileList)
            }
        }
    }

    private fun permissionCards(value: Int) {
        fragmentDashboardBinding.cvCarePoints.visibility = value
        fragmentDashboardBinding.cvLockBox.visibility = value
        fragmentDashboardBinding.cvMedList.visibility =  View.VISIBLE    //value
        fragmentDashboardBinding.cvResources.visibility = value
        fragmentDashboardBinding.cvCareTeam.visibility = View.VISIBLE
        fragmentDashboardBinding.cvVitalStats.visibility = View.VISIBLE
        fragmentDashboardBinding.cvDiscussion.visibility = View.GONE
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
//                showError(requireContext(),"Not implemented")
                findNavController().navigate(R.id.action_dashboard_to_resources)
            }
            R.id.cvLockBox -> {
                findNavController().navigate(R.id.action_dashboard_to_lock_box)
            }
            R.id.cvVitalStats -> {
                showError(requireContext(),"Not implemented")
                //findNavController().navigate(R.id.action_dashboard_to_vital_stats)
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