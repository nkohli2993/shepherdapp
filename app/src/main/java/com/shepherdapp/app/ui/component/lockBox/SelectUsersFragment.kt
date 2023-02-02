package com.shepherdapp.app.ui.component.lockBox

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
import com.shepherdapp.app.databinding.FragmentSelectUsersBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.careTeamMembers.adapter.CareTeamMembersAdapter
import com.shepherdapp.app.ui.component.lockBox.adapter.SelectUsersAdapter
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.view_model.SelectUsersViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Deepak Rattan on 02/02/23
 */
@AndroidEntryPoint
class SelectUsersFragment : BaseFragment<FragmentSelectUsersBinding>(), View.OnClickListener {
    private lateinit var fragmentSelectUsersBinding: FragmentSelectUsersBinding
    private val selectUsersViewModel: SelectUsersViewModel by viewModels()
    private var selectUsersAdapter: SelectUsersAdapter? = null
    private var careTeams: ArrayList<CareTeamModel>? = ArrayList()
    private var searchedCareTeams: ArrayList<CareTeamModel>? = ArrayList()


    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1


    private val TAG = "SelectUsersFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSelectUsersBinding =
            FragmentSelectUsersBinding.inflate(inflater, container, false)

        return fragmentSelectUsersBinding.root
    }

    override fun observeViewModel() {

        observe(selectUsersViewModel.selectedUserPositionLiveData, ::selectedPosition)


        selectUsersViewModel.careTeamsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    careTeams = it.data.payload.data
                    careTeams?.let { it1 -> selectUsersAdapter?.updateCareTeams(it1) }
                }
                is DataResult.Failure -> {
                    hideLoading()
                    careTeams?.clear()
                    careTeams?.let { it1 -> selectUsersAdapter?.updateCareTeams(it1) }
                    fragmentSelectUsersBinding.let {
                        it.recyclerViewCareTeam.visibility = View.GONE
                        it.txtNoCareTeamFound.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun selectedPosition(singleEvent: SingleEvent<Int>) {
        singleEvent.getContentIfNotHandled()?.let {
            careTeams?.get(it)?.isSelected = !careTeams?.get(it)?.isSelected!!
            fragmentSelectUsersBinding.recyclerViewCareTeam.postDelayed({
                selectUsersAdapter?.notifyDataSetChanged()
            }, 100)
        }

    }

    override fun initViewBinding() {
        fragmentSelectUsersBinding.listener = this
        setUsersAdapters()
        //careTeamViewModel.getHomeData()
        val lovedOneUUID = selectUsersViewModel.getLovedOneUUID()
        Log.d(TAG, "lovedOneUUID : $lovedOneUUID")

        // Get Care Teams by lovedOne Id
        selectUsersViewModel.getCareTeamsByLovedOneId(pageNumber, limit, status)


        fragmentSelectUsersBinding.imgCancel.setOnClickListener {
            fragmentSelectUsersBinding.editTextSearch.setText("")
        }
        // Search Care Team Members
        fragmentSelectUsersBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.isEmpty()) {
                        careTeams.let {
                            it?.let { it1 -> selectUsersAdapter?.updateCareTeams(it1) }
                        }
                        fragmentSelectUsersBinding.imgCancel.visibility = View.GONE
                        fragmentSelectUsersBinding.recyclerViewCareTeam.visibility =
                            View.VISIBLE
                    }

                    if (s.isNotEmpty()) {
                        fragmentSelectUsersBinding.imgCancel.visibility = View.VISIBLE
                        searchedCareTeams?.clear()
                        searchedCareTeams = careTeams?.filter {
                            it.user_id_details.firstname?.contains(
                                s,
                                true
                            ) == true
                        } as ArrayList<CareTeamModel>

                        // Show No Care Team Found when no care team is available during search
                        if (searchedCareTeams.isNullOrEmpty()) {
                            fragmentSelectUsersBinding.let {
                                it.recyclerViewCareTeam.visibility = View.GONE
                                it.txtNoCareTeamFound.visibility = View.VISIBLE
                            }
                        } else {
                            fragmentSelectUsersBinding.let {
                                it.recyclerViewCareTeam.visibility = View.VISIBLE
                                it.txtNoCareTeamFound.visibility = View.GONE
                            }
                        }

                        searchedCareTeams.let {
                            it?.let { it1 ->
                                selectUsersAdapter?.updateCareTeams(
                                    it1
                                )
                            }
                        }
                    }
                }

            }
        })


    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_select_users
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnDone -> {
                val selectedUser = careTeams?.filter {
                    it.isSelected
                } as ArrayList<CareTeamModel>

                Log.d(TAG, "selectedUsers count is  : ${selectedUser.size}")

                // Navigate to Add New LockBox Screen with Selected User Data
                val action =
                    SelectUsersFragmentDirections.actionSelectUsersFragmentToAddNewLockBoxFragment(
                        userList = selectedUser.toTypedArray()
                    )
                findNavController().navigate(action)
            }

            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }


    private fun setUsersAdapters() {
        selectUsersAdapter = SelectUsersAdapter(selectUsersViewModel)
        fragmentSelectUsersBinding.recyclerViewCareTeam.adapter = selectUsersAdapter
    }
}