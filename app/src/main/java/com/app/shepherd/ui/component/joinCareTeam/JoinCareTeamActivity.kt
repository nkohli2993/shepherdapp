package com.app.shepherd.ui.component.joinCareTeam

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.ActivityJoinCareTeamBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOneCondition.adapter.JoinCareTeamAdapter
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_join_care_team.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class JoinCareTeamActivity : BaseActivity(), View.OnClickListener {

    private val joinCareTeamViewModel: JoinCareTeamViewModel by viewModels()
    private lateinit var binding: ActivityJoinCareTeamBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolBar.listener = this
        binding.listener = this

        setJoinCareTeamAdapter()
    }


    override fun initViewBinding() {
        binding = ActivityJoinCareTeamBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        observe(joinCareTeamViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(joinCareTeamViewModel.showSnackBar)
        observeToast(joinCareTeamViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { joinCareTeamViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun setJoinCareTeamAdapter() {
        val joinCareTeamAdapter = JoinCareTeamAdapter(joinCareTeamViewModel)
        recyclerViewMembers.adapter = joinCareTeamAdapter
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
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


}

