package com.shepherdapp.app.ui.component.chat

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.added_events.UserAssigneDetail
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.FragmentNewMessageListBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.chat.adapter.AdapterMemberCareTeam
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.TableName
import com.shepherdapp.app.view_model.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewMessageListFragment : BaseFragment<FragmentNewMessageListBinding>(), View.OnClickListener,
    AdapterMemberCareTeam.AssigneeSelected {

    override fun getLayoutRes() = R.layout.fragment_new_message_list
    private var careTeamAdapter: AdapterMemberCareTeam? = null
    private lateinit var fragmentNewMessageBinding: FragmentNewMessageListBinding
    val messagesViewModel: MessagesViewModel by viewModels()
    private var careTeams: ArrayList<CareTeamModel> = ArrayList()
    private var searchedChatList: ArrayList<CareTeamModel> = ArrayList()
    private var pageNumber: Int = 1
    private var limit: Int = 20
    private var status: Int = 1
    var currentPage: Int = 0
    var totalPage: Int = 0
    var total: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::fragmentNewMessageBinding.isInitialized) {
            fragmentNewMessageBinding =
                FragmentNewMessageListBinding.inflate(inflater, container, false)
            messagesViewModel.getCareTeamsByLovedOneId(pageNumber, limit, status)
        }


        return fragmentNewMessageBinding.root
    }

    override fun initViewBinding() {
        fragmentNewMessageBinding.listener = this

    }

    override fun observeViewModel() {
        messagesViewModel.careTeamsResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Loading -> {
                    showLoading("")
                }

                is DataResult.Success -> {
                    hideLoading()
                    careTeams = result.data.payload.data
                    val listSameUser = careTeams.filter {
                        Log.e("catch_exception","list: ${it.user_id} ${messagesViewModel.getUUID()}")
                        it.user_id == messagesViewModel.getUUID()
                    }

                    careTeams.removeAll(listSameUser.toSet())
                    if (careTeams.size <= 0) {
                        fragmentNewMessageBinding.rvUserListing.visibility = View.GONE
                        fragmentNewMessageBinding.textViewNoMessages.visibility = View.VISIBLE
                    }else
                        setCareTeamAdapters()
                }

                is DataResult.Failure -> {
                    hideLoading()
                    careTeams.clear()
                    careTeams.let { it1 -> careTeamAdapter?.updateCareTeams(it1) }
                    fragmentNewMessageBinding.let {
                        it.rvUserListing.visibility = View.GONE
                        it.textViewNoMessages.visibility = View.VISIBLE
                    }

                }
            }
        }
        messagesViewModel.searchCareTeamsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    careTeams.clear()
                    fragmentNewMessageBinding.let {
                        it.rvUserListing.visibility = View.GONE
                        it.textViewNoMessages.visibility = View.VISIBLE
                    }

                }

                is DataResult.Loading -> {
                    //  showLoading("")
                }

                is DataResult.Success -> {
                    hideLoading()
                    careTeams = it.data.payload.data
                    total = it.data.payload.total!!
                    currentPage = it.data.payload.currentPage!!
                    totalPage = it.data.payload.totalPages!!
                    fragmentNewMessageBinding.rvUserListing.visibility = View.VISIBLE
                    fragmentNewMessageBinding.textViewNoMessages.visibility = View.GONE
                    if (careTeams.isNullOrEmpty()) return@observeEvent
                    val loggedInUserUUID =
                        Prefs.with(ShepherdApp.appContext)!!.getString(Const.UUID, "")
                    // User list should not contain loggedIn User
                    val careTeamList = careTeams.filterNot { careTeamModel ->
                        careTeamModel.user_id_details?.uid == loggedInUserUUID
                    } as ArrayList

                    careTeamList.filter {
                        it.user_id != messagesViewModel.getUUID()
                    }

                    if (careTeamList.size <= 0) {
                        fragmentNewMessageBinding.rvUserListing.visibility = View.GONE
                        fragmentNewMessageBinding.textViewNoMessages.visibility = View.VISIBLE
                    }
                    careTeamAdapter?.updateCareTeams(careTeamList)
                }
            }
        }

    }


    private fun setCareTeamAdapters() {
        careTeamAdapter = AdapterMemberCareTeam(careTeams, this)
        fragmentNewMessageBinding.rvUserListing.adapter = careTeamAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgCancel -> {
                fragmentNewMessageBinding.editTextSearch.setText("")
                fragmentNewMessageBinding.imgCancel.isVisible = false
                fragmentNewMessageBinding.textViewNoMessages.visibility = View.GONE
                fragmentNewMessageBinding.rvUserListing.visibility = View.VISIBLE
                careTeamAdapter?.updateCareTeams(careTeams)
            }

            R.id.ivBack -> {
                findNavController().popBackStack()
            }

        }
    }


    override fun onAssigneeSelected(detail: CareTeamModel) {
        val careDetail = UserAssigneDetail(
            detail.user_id_details!!.id,
            detail.user_id_details!!.id,
            detail.user_id_details!!.firstname,
            detail.user_id_details!!.lastname,
            detail.user_id_details!!.dob,
            detail.user_id_details!!.address,
            detail.user_id_details!!.phone,
            detail.user_id_details!!.phone,
            detail.user_id_details!!.profilePhoto,
            null, null, "", "", ""
        )

        findNavController().navigate(
            R.id.action_nav_assignee_to_nav_chat,
            bundleOf("assignee_user" to careDetail)
        )
    }

    override fun onResume() {
        super.onResume()
        fragmentNewMessageBinding.editTextSearch.doAfterTextChanged { search ->
            if (!search.isNullOrEmpty()) {
                fragmentNewMessageBinding.imgCancel.isVisible = true
                messagesViewModel.searchCareTeamsByLovedOneId(
                    pageNumber,
                    limit,
                    status,
                    search.toString()
                )
            } else {
                fragmentNewMessageBinding.imgCancel.isVisible = false
                careTeams.clear()
                messagesViewModel.getCareTeamsByLovedOneId(pageNumber, limit, status)
            }

        }
    }
}