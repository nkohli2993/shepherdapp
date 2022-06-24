package com.app.shepherd.ui.component.careTeamMembers

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.databinding.FragmentCareTeamMembersBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.careTeamMembers.adapter.CareTeamMembersAdapter
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.observe
import com.app.shepherd.view_model.CareTeamMembersViewModel
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
    private var limit: Int = 10
    private var status: Int = 1

    private var careTeams: ArrayList<CareTeam>? = ArrayList()
    private var careTeamAdapter: CareTeamMembersAdapter? = null


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
        careTeamViewModel.getCareTeams(pageNumber, limit, status)

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
                    careTeams = it.data.payload.careteams
                    if (careTeams.isNullOrEmpty()) return@observeEvent
                    careTeamAdapter?.updateCareTeams(careTeams!!)

                    // binding.layoutCareTeam.visibility = View.VISIBLE
                    // binding.txtNoCareTeamFound.visibility = View.GONE
                }

                is DataResult.Failure -> {
                    //handleAPIFailure(it.message, it.errorCode)

                    hideLoading()
                    // it.message?.let { showError(this, it.toString()) }
                    //binding.layoutCareTeam.visibility = View.GONE
                    //binding.txtNoCareTeamFound.visibility = View.VISIBLE
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

    private fun openMemberDetails(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
            findNavController().navigate(R.id.action_care_team_members_to_member_details)
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

