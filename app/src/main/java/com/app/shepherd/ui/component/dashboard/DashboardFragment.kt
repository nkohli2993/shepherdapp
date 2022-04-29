package com.app.shepherd.ui.component.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private lateinit var fragmentDashboardBinding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private var adapter: DashboardAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.inflateDashboardList(requireContext())
        adapter = DashboardAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentDashboardBinding = FragmentDashboardBinding.inflate(inflater, container, false)

        fragmentDashboardBinding.recyclerView.adapter = adapter
        initRecyclerView(viewModel.dashboardItemList)

        return fragmentDashboardBinding.root
    }


    private fun initRecyclerView(dashboard: ArrayList<DashboardModel>) {
        adapter?.addData(dashboard)
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_dashboard
    }

    override fun observeViewModel() {
        observeEvent(viewModel.openDashboardItems, ::navigateToDashboardItems)
    }

    override fun initViewBinding() {
    }

    private fun navigateToDashboardItems(navigateEvent: SingleEvent<DashboardModel>) {
        navigateEvent.getContentIfNotHandled()?.let {
            if (it.title == resources.getString(R.string.care_team)) {
                findNavController().navigate(R.id.action_dashboard_to_care_team_members)
            } else if (it.title == resources.getString(R.string.care_points)) {
                findNavController().navigate(R.id.action_dashboard_to_add_new_event)
            } else if (it.title == resources.getString(R.string.lock_box)) {
                findNavController().navigate(R.id.action_dashboard_to_event_details)
            }

        }

    }

}