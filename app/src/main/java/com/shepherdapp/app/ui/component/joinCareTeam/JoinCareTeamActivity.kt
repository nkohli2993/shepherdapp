package com.shepherdapp.app.ui.component.joinCareTeam

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.invitation.Results
import com.shepherdapp.app.databinding.ActivityJoinCareTeamBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.joinCareTeam.adapter.JoinCareTeamAdapter
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Invitations
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.Status
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.CareTeamsViewModel
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
    private var careTeams: ArrayList<Results>? = ArrayList()
    private var selectedCareTeams: ArrayList<Results>? = ArrayList()
    private var joinCareTeamAdapter: JoinCareTeamAdapter? = null
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var accepted = 0
    //    private var status: Int = 1
    private var sendType: String = Invitations.Receiver.sendType
    private var status = Status.Zero.status
    private var results: ArrayList<Results>? = ArrayList()
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding.ivBack.listener = this
        binding.listener = this
        binding.recyclerViewMembers.layoutManager = LinearLayoutManager(this)
        setJoinCareTeamAdapter()
//        careTeamsViewModel.getCareTeamsForLoggedInUser(pageNumber, limit, status)
        //Get Invitations for Joining Care Team
        careTeamsViewModel.getJoinCareTeamInvitations(sendType, status)
    }


    override fun initViewBinding() {
        binding = ActivityJoinCareTeamBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        careTeamsViewModel.invitationsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    //showError(requireContext(), it.message.toString())

                    results?.clear()
                    results?.let { it1 -> joinCareTeamAdapter?.updateCareTeams(it1) }
                    noInvitationFound()
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    careTeamsViewModel.saveSignUp(false)
                    results = it.data.payload?.results
                    if (results.isNullOrEmpty()) return@observeEvent
                    joinCareTeamAdapter?.updateCareTeams(results!!)

                    binding.layoutCareTeam.visibility = View.VISIBLE
                    binding.txtNoCareTeamFound.visibility = View.GONE
                }
            }
        }

        careTeamsViewModel.acceptInvitationsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(this, it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    if (accepted == 0) {
                        val lovedOneUUID = it.data.payload?.loveoneUserId
                        lovedOneUUID?.let { it1 -> careTeamsViewModel.saveLovedOneUUID(it1) }
                    }
                    accepted++
                    careTeamsViewModel.saveSignUp(false)
                    showSuccess(this, "Invitation Accepted Successfully...")
                    // Save LovedOne UUID


                    if (accepted == 0) {
                        showInfo(this, "Please accept any one invitation to join...")
                    } else {
                        navigateToDashboardScreen()
                    }
//                    results!![position].isSelected = true
//                    joinCareTeamAdapter?.updateCareTeams(results!!)
                    // Refresh the invitations
//                    careTeamsViewModel.getJoinCareTeamInvitations(sendType, status)
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
            R.id.ivBack -> {
                //finishActivity()
                onBackPressed()
            }
            R.id.buttonJoin -> {
                //navigateToDashboardScreen()
//                val lovedOneUUID = Prefs.with(ShepherdApp.appContext)!!
//                    .getString(Const.LOVED_ONE_UUID, "")
                if (accepted == 0) {
                    showInfo(this, "Please accept any one invitation to join...")
                } else {
                    navigateToDashboardScreen()
                }
            }
        }
    }

    private fun navigateToDashboardScreen() {
        startActivityWithFinish<HomeActivity>()
    }

    override fun onItemClick(result: Results?,position:Int) {
        if (result?.id != null) {
            Prefs.with(ShepherdApp.appContext)!!.save(Const.USER_ROLE, result.careRoles?.name)
            this.position = position
            // Accept the invitation request
            careTeamsViewModel.acceptCareTeamInvitations(result.id!!)
        }

        /* if (selectedCareTeams?.isEmpty() == true)
             result.let { it?.let { it1 -> selectedCareTeams?.add(it1) } }
         else if (result?.isSelected == true) selectedCareTeams?.add(result)
         else if (result?.isSelected == false && selectedCareTeams?.contains(result) == true)
             selectedCareTeams?.remove(result)*/
    }

    /*override fun onItemClick(careTeam: CareTeam) {
        if (selectedCareTeams?.isEmpty() == true)
            careTeam.let { selectedCareTeams?.add(it) }
        else if (careTeam.isSelected == true) selectedCareTeams?.add(careTeam)
        else if (careTeam.isSelected == false && selectedCareTeams?.contains(careTeam) == true)
            selectedCareTeams?.remove(careTeam)
    }*/

    fun noInvitationFound() {
        binding.txtNoCareTeamFound.visibility = View.VISIBLE
//        binding.buttonJoin.visibility = View.GONE
    }
}




