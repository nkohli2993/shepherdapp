package com.app.shepherd.ui.component.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.FragmentDashboardBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.dashboard.adapter.DashboardAdapter
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(),
    View.OnClickListener {

    private lateinit var fragmentDashboardBinding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()


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
        return fragmentDashboardBinding.root
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_dashboard
    }

    override fun observeViewModel() {
        observeEvent(viewModel.openDashboardItems, ::navigateToDashboardItems)
    }

    override fun initViewBinding() {
        fragmentDashboardBinding.listener = this
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

}