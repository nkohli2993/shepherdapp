package com.app.shepherd.ui.component.joinCareTeam

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.shepherd.R
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.invitation.Results
import com.app.shepherd.databinding.ActivityJoinCareTeamBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.ui.component.joinCareTeam.adapter.JoinCareTeamAdapter
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Invitations
import com.app.shepherd.utils.Prefs
import com.app.shepherd.utils.Status
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showInfo
import com.app.shepherd.utils.extensions.showSuccess
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
    private var careTeams: ArrayList<Results>? = ArrayList()
    private var selectedCareTeams: ArrayList<Results>? = ArrayList()
    private var joinCareTeamAdapter: JoinCareTeamAdapter? = null
    private var pageNumber: Int = 1
    private var limit: Int = 10

    //    private var status: Int = 1
    private var sendType: String = Invitations.Receiver.sendType
    private var status = Status.Zero.status
    private var results: ArrayList<Results>? = ArrayList()


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
        /* careTeamsViewModel.careTeamsResponseLiveData.observeEvent(this) {
             when (it) {
                 is DataResult.Loading -> {
                     showLoading("")
                 }
                 is DataResult.Success -> {
                     hideLoading()
                     careTeams = it.data.payload.careTeams
                     if (careTeams.isNullOrEmpty()) return@observeEvent
                     joinCareTeamAdapter?.updateCareTeams(careTeams!!)

                     binding.layoutCareTeam.visibility = View.VISIBLE
                     binding.txtNoCareTeamFound.visibility = View.GONE
                 }

                 is DataResult.Failure -> {
                     //handleAPIFailure(it.message, it.errorCode)

                     hideLoading()
                     // it.message?.let { showError(this, it.toString()) }
                     //binding.layoutCareTeam.visibility = View.GONE
                     //binding.txtNoCareTeamFound.visibility = View.VISIBLE
                     val builder = AlertDialog.Builder(this)
                     val dialog = builder.apply {
                         setTitle("Care Teams")
                         setMessage("No Care Team Found")
                         setPositiveButton("OK") { _, _ ->
                             navigateToDashboardScreen()
                         }
                     }.create()
                     dialog.show()
                     dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                 }
             }
         }*/

        careTeamsViewModel.invitationsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    //showError(requireContext(), it.message.toString())

                    results?.clear()
                    results?.let { it1 -> joinCareTeamAdapter?.updateCareTeams(it1) }

                    val builder = AlertDialog.Builder(this)
                    val dialog = builder.apply {
                        setTitle("Join Care Team Invitations")
                        setMessage("No Invitations Found")
                        setPositiveButton("OK") { _, _ ->
                            // navigateToDashboardScreen()
                            // Check if SharedPref contains lovedOne UUID
                            val lovedOneUUID = Prefs.with(ShepherdApp.appContext)!!
                                .getString(Const.LOVED_ONE_UUID, "")
                            if (lovedOneUUID.isNullOrEmpty()) {
                                onBackPressed()
                            } else {
                                navigateToDashboardScreen()
                            }
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
                    showSuccess(this, "Invitation Accepted Successfully...")
                    // Save LovedOne UUID
                    val lovedOneUUID = it.data.payload?.loveoneUserId
                    lovedOneUUID?.let { it1 -> careTeamsViewModel.saveLovedOneUUID(it1) }
                    // Refresh the invitations
                    careTeamsViewModel.getJoinCareTeamInvitations(sendType, status)
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
                val lovedOneUUID = Prefs.with(ShepherdApp.appContext)!!
                    .getString(Const.LOVED_ONE_UUID, "")
                if (lovedOneUUID.isNullOrEmpty()) {
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

    override fun onItemClick(result: Results?) {
        if (result?.id != null) {
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
}




