package com.app.shepherd.ui.component.joinCareTeam

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.shepherd.R
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.databinding.ActivityJoinCareTeamBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.ui.component.joinCareTeam.adapter.JoinCareTeamAdapter
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.view_model.CareTeamsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_join_care_team.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class JoinCareTeamActivity : BaseActivity(), View.OnClickListener,
    JoinCareTeamAdapter.OnItemClickListener {

    private val careTeamsViewModel: CareTeamsViewModel by viewModels()
    private lateinit var binding: ActivityJoinCareTeamBinding
    private var careTeams: ArrayList<CareTeam>? = ArrayList()
    private var selectedCareTeams: ArrayList<CareTeam>? = ArrayList()
    private var joinCareTeamAdapter: JoinCareTeamAdapter? = null
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolBarNew.listener = this
        binding.listener = this
        binding.recyclerViewMembers.layoutManager = LinearLayoutManager(this)
        setJoinCareTeamAdapter()
        careTeamsViewModel.getCareTeams(pageNumber, limit, status)
    }


    override fun initViewBinding() {
        binding = ActivityJoinCareTeamBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        careTeamsViewModel.careTeamsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    careTeams = it.data.payload.careteams
                    if (careTeams.isNullOrEmpty()) return@observeEvent
                    joinCareTeamAdapter?.updateCareTeams(careTeams!!)

                    binding.layoutCareTeam.visibility = View.VISIBLE
                    binding.txtNoCareTeamFound.visibility = View.GONE
                }

                is DataResult.Failure -> {
                    //handleAPIFailure(it.message, it.errorCode)

                    hideLoading()
                   // it.message?.let { showError(this, it.toString()) }
                    binding.layoutCareTeam.visibility = View.GONE
                    binding.txtNoCareTeamFound.visibility = View.VISIBLE

                }
            }
        }
    }

    private fun setJoinCareTeamAdapter() {
        joinCareTeamAdapter = JoinCareTeamAdapter(careTeamsViewModel)
        joinCareTeamAdapter?.setClickListener(this)
        recyclerViewMembers.adapter = joinCareTeamAdapter
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                finishActivity()
            }
            R.id.buttonJoin -> {
                navigateToDashboardScreen()
            }
        }
    }

    private fun navigateToDashboardScreen() {
        startActivityWithFinish<HomeActivity>()
    }

    override fun onItemClick(careTeam: CareTeam) {
        if (selectedCareTeams?.isEmpty() == true)
            careTeam.let { selectedCareTeams?.add(it) }
        else if (careTeam.isSelected == true) selectedCareTeams?.add(careTeam)
        else if (careTeam.isSelected == false && selectedCareTeams?.contains(careTeam) == true)
            selectedCareTeams?.remove(careTeam)
    }
}




