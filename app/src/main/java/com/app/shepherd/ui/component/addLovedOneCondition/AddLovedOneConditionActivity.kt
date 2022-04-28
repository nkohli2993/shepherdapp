package com.app.shepherd.ui.component.addLovedOneCondition

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.ActivityAddLovedOneConditionBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
import com.app.shepherd.ui.component.addLovedOneCondition.adapter.AddLovedOneConditionAdapter
import com.app.shepherd.ui.component.joinCareTeam.JoinCareTeamActivity
import com.app.shepherd.ui.component.welcome.WelcomeActivity
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_loved_one_condition.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddLovedOneConditionActivity : BaseActivity(), View.OnClickListener {

    private val addLovedOneConditionViewModel: AddLovedOneConditionViewModel by viewModels()
    private lateinit var binding: ActivityAddLovedOneConditionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolBar.listener = this
        binding.listener = this

        setConditionAdapter()
    }


    override fun initViewBinding() {
        binding = ActivityAddLovedOneConditionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        observe(addLovedOneConditionViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(addLovedOneConditionViewModel.showSnackBar)
        observeToast(addLovedOneConditionViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {
            }
            is Resource.DataError -> {
                status.errorCode?.let { addLovedOneConditionViewModel.showToastMessage(it) }
            }
        }
    }


    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setConditionAdapter() {
        val addLovedOneConditionAdapter = AddLovedOneConditionAdapter(addLovedOneConditionViewModel)
        recyclerViewCondition.adapter = addLovedOneConditionAdapter
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                finishActivity()
            }
            R.id.buttonFinish -> {
                navigateToWelcomeScreen()
            }
        }
    }


    private fun navigateToWelcomeScreen() {
        startActivityWithFinishAffinity<WelcomeActivity>()
    }

}

