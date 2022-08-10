package com.shepherd.app.ui.component.careTeamMembers

import android.app.AlertDialog
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
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.databinding.FragmentCareTeamMembersBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.careTeamMembers.adapter.CareTeamMembersAdapter
import com.shepherd.app.utils.CareRole
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.CareTeamMembersViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class CareTeamMembersFragment : BaseFragment<FragmentCareTeamMembersBinding>(),
    View.OnClickListener {

    private val careTeamViewModel: CareTeamMembersViewModel by viewModels()

    private lateinit var fragmentCareTeamMembersBinding: FragmentCareTeamMembersBinding

    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1

    private var careTeams: ArrayList<CareTeamModel>? = ArrayList()
    private var selectedCareTeams: ArrayList<CareTeamModel>? = ArrayList()
    private var searchedCareTeams: ArrayList<CareTeamModel>? = ArrayList()
    private var careTeamAdapter: CareTeamMembersAdapter? = null
    private var TAG = "CareTeamMembersFragment"


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
                            it.love_user_id_details.firstname?.startsWith(
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


    }

    override fun observeViewModel() {
        observe(careTeamViewModel.openMemberDetails, ::openMemberDetails)

        careTeamViewModel.careTeamsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    careTeams = it.data.payload.data
                    if (careTeams.isNullOrEmpty()) return@observeEvent
                    // Get the uuid of Care Team Leader
                    try {
                        val uuidTeamLead = careTeams?.filter {
                            it.careRoles.slug == CareRole.CareTeamLead.slug
                        }?.map {
                            it.user_id
                        }?.get(0)
                        Log.d(TAG, "Care team Leader UUID : $uuidTeamLead ")
                        uuidTeamLead?.let { it1 -> careTeamViewModel.saveLoggedInUserTeamLead(it1) }
                    }catch (e:Exception){
                        Log.d(TAG,"Error: ${e.toString()}")
                    }
                    careTeamAdapter?.updateCareTeams(careTeams!!)
                }
                is DataResult.Failure -> {
                    //handleAPIFailure(it.message, it.errorCode)

                    hideLoading()
                    // it.message?.let { showError(this, it.toString()) }
                    //binding.layoutCareTeam.visibility = View.GONE
                    //binding.txtNoCareTeamFound.visibility = View.VISIBLE
                    careTeams?.clear()
                    careTeams?.let { it1 -> careTeamAdapter?.updateCareTeams(it1) }

                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Care Teams")
                        setMessage("No Care Team Found")
                        setPositiveButton("OK") { _, _ ->
                            // navigateToDashboardScreen()
                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
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


}

