package com.shepherdapp.app.ui.component.careTeamMembers

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.FragmentCareTeamMembersBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherdapp.app.ui.base.listeners.UpdateViewOfParentListener
import com.shepherdapp.app.ui.component.careTeamMembers.adapter.CareTeamMembersAdapter
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.utils.CareRole
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.view_model.CareTeamMembersViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class CareTeamMembersFragment : BaseFragment<FragmentCareTeamMembersBinding>(),
    View.OnClickListener {

    private val careTeamViewModel: CareTeamMembersViewModel by viewModels()

    private lateinit var fragmentCareTeamMembersBinding: FragmentCareTeamMembersBinding

    private var pageNumber: Int = 1
    private var limit: Int = 20
    private var status: Int = 1

    private var careTeams: ArrayList<CareTeamModel>? = ArrayList()
    private var searchedCareTeams: ArrayList<CareTeamModel>? = ArrayList()
    private var careTeamAdapter: CareTeamMembersAdapter? = null
    private var TAG = "CareTeamMembersFragment"

    private var parentActivityListener: ChildFragmentToActivityListener? = null
    private var updateViewOfParentListenerListener: UpdateViewOfParentListener? = null

    private lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
        if (context is ChildFragmentToActivityListener) parentActivityListener = context
        if (context is UpdateViewOfParentListener) updateViewOfParentListenerListener = context
        else throw RuntimeException("$context must implement ChildFragmentToActivityListener")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCareTeamMembersBinding =
            FragmentCareTeamMembersBinding.inflate(inflater, container, false)

        return fragmentCareTeamMembersBinding.root
    }

    override fun initViewBinding() {
        fragmentCareTeamMembersBinding.listener = this
        setCareTeamAdapters()

        //careTeamViewModel.getHomeData()
        val lovedOneUUID = careTeamViewModel.getLovedOneUUID()
        Log.d(TAG, "lovedOneUUID : $lovedOneUUID")

        // Get Care Teams by lovedOne Id
        careTeamViewModel.getCareTeamsByLovedOneId(pageNumber, limit, status)

        fragmentCareTeamMembersBinding.imgCancel.setOnClickListener {
            fragmentCareTeamMembersBinding.editTextSearch.setText("")
        }

        // Search Care Team Members
        fragmentCareTeamMembersBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.isEmpty()) {
                        careTeams.let {
                            it?.let { it1 -> careTeamAdapter?.updateCareTeams(it1) }
                        }
                        fragmentCareTeamMembersBinding.imgCancel.visibility = View.GONE
                        fragmentCareTeamMembersBinding.recyclerViewCareTeam.visibility =
                            View.VISIBLE
                    }

                    if (s.isNotEmpty()) {
                        fragmentCareTeamMembersBinding.imgCancel.visibility = View.VISIBLE
                        searchedCareTeams?.clear()
                        searchedCareTeams = careTeams?.filter {
                            it.user_id_details?.firstname?.contains(
                                s,
                                true
                            ) == true
                        } as ArrayList<CareTeamModel>

                        // Show No Care Team Found when no care team is available during search
                        if (searchedCareTeams.isNullOrEmpty()) {
                            fragmentCareTeamMembersBinding.let {
                                it.recyclerViewCareTeam.visibility = View.GONE
                                it.txtNoCareTeamFound.visibility = View.VISIBLE
                            }
                        } else {
                            fragmentCareTeamMembersBinding.let {
                                it.recyclerViewCareTeam.visibility = View.VISIBLE
                                it.txtNoCareTeamFound.visibility = View.GONE
                            }
                        }

                        searchedCareTeams.let {
                            it?.let { it1 ->
                                careTeamAdapter?.updateCareTeams(
                                    it1
                                )
                            }
                        }
                    }
                }

            }
        })


        // Update the visibility of New Button if LoggedIn User is the CareTeam Leader
        if (careTeamViewModel.isLoggedInUserCareTeamLead() == true) {
            updateViewOfParentListenerListener?.updateViewVisibility(true)
        } else {
            updateViewOfParentListenerListener?.updateViewVisibility(false)
        }

    }

    override fun observeViewModel() {
        observe(careTeamViewModel.openMemberDetails, ::openMemberDetails)
        observe(careTeamViewModel.deletePendingInviteLiveData, ::deletePendingInvite)

        careTeamViewModel.pendingInviteResponseLiveData.observeEvent(this) { it ->
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    if (careTeams.isNullOrEmpty()) return@observeEvent
                    // Get the uuid of Care Team Leader
                    try {
                        val uuidTeamLead = careTeams?.filter {
                            it.careRoles?.slug == CareRole.CareTeamLead.slug
                        }?.map {
                            it.user_id
                        }?.get(0)
                        Log.d(TAG, "Care team Leader UUID : $uuidTeamLead ")
                        uuidTeamLead?.let { it1 -> careTeamViewModel.saveLoggedInUserTeamLead(it1) }
                    } catch (e: Exception) {
                        Log.d(TAG, "Error: ${e.toString()}")
                    }
                    fragmentCareTeamMembersBinding.let {
                        it.recyclerViewCareTeam.visibility = View.VISIBLE
                        it.txtNoCareTeamFound.visibility = View.GONE
                    }

                    // Move CareTeam Leader to first position
                    val careTeamList = moveCareTeamLeaderToFirstPosition(careTeams)
                    careTeamList?.let { it1 -> careTeamAdapter?.updateCareTeams(it1) }

                }
                is DataResult.Loading -> {
                    showLoading("")

                }
                is DataResult.Success -> {
                    hideLoading()
                    val results = it.data.payload?.results
                    Log.d(TAG, "Pending Invite : Payload is $results")
                    results?.forEach {
                        it.isPendingInvite = true
                    }

                    if (results != null) {
                        careTeams?.addAll(results)
                    }

                    if (careTeams.isNullOrEmpty()) return@observeEvent
                    // Get the uuid of Care Team Leader
                    try {
                        val uuidTeamLead = careTeams?.filter {
                            it.careRoles?.slug == CareRole.CareTeamLead.slug
                        }?.map {
                            it.user_id
                        }?.get(0)
                        Log.d(TAG, "Care team Leader UUID : $uuidTeamLead ")
                        uuidTeamLead?.let { it1 -> careTeamViewModel.saveLoggedInUserTeamLead(it1) }
                    } catch (e: Exception) {
                        Log.d(TAG, "Error: ${e.toString()}")
                    }
                    fragmentCareTeamMembersBinding.let {
                        it.recyclerViewCareTeam.visibility = View.VISIBLE
                        it.txtNoCareTeamFound.visibility = View.GONE
                    }

                    // Move CareTeam Leader to first position
                    val careTeamList = moveCareTeamLeaderToFirstPosition(careTeams)
                    careTeamList?.let { it1 -> careTeamAdapter?.updateCareTeams(it1) }
                }
            }
        }

        careTeamViewModel.careTeamsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    // Get Pending Invites
                    careTeamViewModel.getPendingInvites()
                    careTeams = it.data.payload.data
                }
                is DataResult.Failure -> {
                    hideLoading()
                    careTeams?.clear()
                    careTeams?.let { it1 -> careTeamAdapter?.updateCareTeams(it1) }
                    fragmentCareTeamMembersBinding.let {
                        it.recyclerViewCareTeam.visibility = View.GONE
                        it.txtNoCareTeamFound.visibility = View.VISIBLE
                    }

                }
            }
        }

        // Observe Response of delete pending invitee by Id
        careTeamViewModel.deletePendingInviteeByIdResponseLiveData.observeEvent(this) {
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
                    showSuccess(requireContext(), it.data.message.toString())
                    // Get Care Teams by lovedOne Id
                    careTeamViewModel.getCareTeamsByLovedOneId(pageNumber, limit, status)
                }
            }
        }
    }

    private fun setCareTeamAdapters() {
        careTeamAdapter = CareTeamMembersAdapter(careTeamViewModel)
        fragmentCareTeamMembersBinding.recyclerViewCareTeam.adapter = careTeamAdapter
    }

    private fun openMemberDetails(navigateEvent: SingleEvent<CareTeamModel>) {
        navigateEvent.getContentIfNotHandled()?.let {
            Log.d(TAG, "openMemberDetails: CareTeam :$it")
            // Sending CareTeam object through safeArgs
            val action = CareTeamMembersFragmentDirections.actionCareTeamMembersToMemberDetails(it)
            //findNavController().navigate(R.id.action_care_team_members_to_member_details)
            findNavController().navigate(action)
        }
    }

    private fun deletePendingInvite(singleEvent: SingleEvent<CareTeamModel>) {
        singleEvent.getContentIfNotHandled()?.let {
            Log.d(TAG, "deletePendingInvite: $it")
            if (careTeamViewModel.isLoggedInUserCareTeamLead() == true) {
                //show delete dialog
                val builder = AlertDialog.Builder(requireContext())
                val dialog = builder.apply {
                    setTitle("Delete Pending Invitee")
                    setMessage("Are you sure, you want to delete this pending invitee?")
                    setPositiveButton("Yes") { _, _ ->
                        it.id?.let { it1 -> careTeamViewModel.deletePendingInviteeById(it1) }
                    }
                    setNegativeButton("Cancel") { _, _ ->
                    }
                }.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            } else {
                showInfo(requireContext(), "Only CareTeam Leader can delete the pending invitee")
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            /* R.id.buttonAddNewMember -> {
                 findNavController().navigate(R.id.action_care_team_members_to_add_team_member)
             }*/
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_team_members
    }

    override fun onResume() {
        parentActivityListener?.msgFromChildFragmentToActivity()
        super.onResume()
    }

    // Move CareTeam Leader to first position
    private fun moveCareTeamLeaderToFirstPosition(careTeams: ArrayList<CareTeamModel>?): ArrayList<CareTeamModel>? {
        // Get List of CareTeam Leader
        val careTeamLeadList = careTeams?.filter {
            it.careRoles?.slug == CareRole.CareTeamLead.slug
        } as ArrayList

        Log.d(TAG, "CareTeam Leader List : $careTeamLeadList")
        // Remove CareTeam Leaders from CareTeam
        careTeams.removeAll(careTeamLeadList)
        // Add Remaining elements of careTeams to CareTeamLeadList
        careTeamLeadList.addAll(careTeams)
        return careTeamLeadList
    }
}

